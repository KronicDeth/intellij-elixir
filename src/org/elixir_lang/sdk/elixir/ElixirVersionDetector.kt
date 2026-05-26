package org.elixir_lang.sdk.elixir

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.UserDataHolderEx
import com.intellij.util.concurrency.ThreadingAssertions
import com.intellij.util.concurrency.annotations.RequiresBackgroundThread
import org.elixir_lang.sdk.wsl.wslCompat
import org.elixir_lang.util.runWithEdtGuard
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import java.util.regex.Pattern

object ElixirVersionDetector {
    val ELIXIR_VERSION_KEY = Key.create<String>("ELIXIR_CANONICAL_VERSION")
    private val LOG = Logger.getInstance(ElixirVersionDetector::class.java)
    private val versionByHomePath: MutableMap<String, String> = ConcurrentHashMap()

    /** Matches `{vsn, "X.Y.Z"}` (or any quoted value) anywhere in an Erlang `.app` file.
     *  Allows optional whitespace around the key, comma, value, and closing brace. */
    private val VSN_PATTERN: Pattern = Pattern.compile("""\{\s*vsn\s*,\s*"([^"]+)"\s*}""")

    /**
     * Reads the Elixir version from `<canonicalHome>/lib/elixir/ebin/elixir.app`.
     *
     * The `.app` file is the standard Erlang application resource file - a plain-text term of the
     * form `{application, elixir, [{vsn, "X.Y.Z"}, ...]}.`. Parsing the `vsn` field via regex is
     * safe because the value is always a simple string literal in OTP `.app` files.
     *
     * Entries are cached by `"$canonicalHome@$appFileMtime"` so stale entries are evicted
     * automatically when the Elixir installation is replaced without restarting the IDE.
     *
     * Must NOT be called on the EDT - file I/O on WSL UNC paths (`\\wsl.localhost\...`)
     * goes through the Plan 9 filesystem redirector and can block for 50–200 ms.
     */
    @RequiresBackgroundThread
    private fun readElixirAppVersion(canonicalHome: String): String? {
        ThreadingAssertions.assertBackgroundThread()
        val appFile = File(canonicalHome, "lib/elixir/ebin/elixir.app")
        if (!appFile.exists()) {
            LOG.debug("elixir.app not found at ${appFile.path}")
            return null
        }
        val lastModified = appFile.lastModified()
        val cacheKey = "$canonicalHome@$lastModified"
        versionByHomePath[cacheKey]?.let { return it }
        return try {
            val content = appFile.readText(Charsets.UTF_8)
            val matcher = VSN_PATTERN.matcher(content)
            if (matcher.find()) {
                val version = matcher.group(1).trim()
                if (version.isNotEmpty()) {
                    versionByHomePath[cacheKey] = version
                    version
                } else {
                    LOG.debug("Empty vsn field in ${appFile.path}")
                    null
                }
            } else {
                LOG.debug("Could not find vsn field in ${appFile.path}")
                null
            }
        } catch (e: Exception) {
            LOG.debug("Failed to read ${appFile.path}", e)
            null
        }
    }

    internal fun elixirVersion(sdkHome: String, resolvedVersion: String?): String? {
        if (!resolvedVersion.isNullOrBlank()) {
            return resolvedVersion
        }
        val canonicalHome = wslCompat.canonicalizePath(sdkHome)
        return runWithEdtGuard("Detecting Elixir SDK version...") {
            readElixirAppVersion(canonicalHome)
        }
    }

    /**
     * Returns the bare canonical Elixir version string for [sdk] (e.g. `"1.15.7"`), or `null`
     * if the SDK is not an Elixir SDK or the version cannot be determined.
     *
     * The result is cached on the [sdk] instance via [UserDataHolderEx] after the first call.
     * On a cold start (after IDE restart, before any SDK setup has run), the backing
     * [ElixirVersionDetector.elixirVersion] reads `elixir.app` from the SDK home directory.
     * That file I/O **must not** happen while a read lock is held - doing so blocks write
     * actions and may be very slow on WSL UNC paths.  Always call this from a background
     * thread or an IO coroutine dispatcher, never from inside `readAction { }`.
     */
    @RequiresBackgroundThread
    fun canonicalVersion(sdk: Sdk): String? {
        ThreadingAssertions.assertBackgroundThread()

        if (sdk.sdkType !== Type.instance) return null

        sdk.getUserData(ELIXIR_VERSION_KEY)?.let { return it }

        // Cold path: reads elixir.app - must not be called under a read lock.
        check(!ApplicationManager.getApplication().holdsReadLock()) {
            "canonicalVersion() reads elixir.app and must not be called under a read lock"
        }

        val homePath = sdk.homePath ?: return null
        val version = elixirVersion(homePath, null) ?: return null

        val existing = (sdk as? UserDataHolderEx)?.putUserDataIfAbsent(ELIXIR_VERSION_KEY, version)
        if (existing != null) {
            return existing
        }

        sdk.putUserData(ELIXIR_VERSION_KEY, version)
        return version
    }
}
