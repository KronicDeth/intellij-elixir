package org.elixir_lang.sdk.elixir

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.UserDataHolderEx
import com.intellij.util.concurrency.ThreadingAssertions
import com.intellij.util.concurrency.annotations.RequiresBackgroundThread
import org.elixir_lang.cli.getExecutableFilepathWslSafe
import org.elixir_lang.jps.shared.cli.CliTool
import org.elixir_lang.sdk.ProcessOutput
import org.elixir_lang.sdk.wsl.wslCompat
import org.elixir_lang.util.runWithEdtGuard
import org.jetbrains.annotations.Contract
import java.io.File
import java.util.concurrent.ConcurrentHashMap

object ElixirVersionDetector {
    val ELIXIR_VERSION_KEY = Key.create<String>("ELIXIR_CANONICAL_VERSION")
    private val LOG = Logger.getInstance(ElixirVersionDetector::class.java)
    private val versionByHomePath: MutableMap<String, String> = ConcurrentHashMap()

    internal fun elixirVersion(sdkHome: String, resolvedVersion: String?): String? {
        if (!resolvedVersion.isNullOrBlank()) {
            return resolvedVersion
        }

        val canonicalHome = wslCompat.canonicalizePath(sdkHome)
        val exePath = CliTool.ELIXIR.getExecutableFilepathWslSafe(canonicalHome)
        val exeFile = File(exePath)
        val lastModified = exeFile.lastModified()
        if (lastModified == 0L) {
            LOG.debug("Elixir executable missing or unreadable: $exePath")
            return elixirVersionFromHomePath(canonicalHome)
        }

        val cacheKey = "$canonicalHome@$lastModified"
        val cached = versionByHomePath[cacheKey]
        if (cached != null || versionByHomePath.containsKey(cacheKey)) {
            return cached
        }

        return runWithEdtGuard("Detecting Elixir SDK version...") {
            elixirVersionBackground(canonicalHome, exePath, cacheKey)
        }
    }

    private fun elixirVersionBackground(canonicalHome: String, exePath: String, cacheKey: String): String? {
        return try {
            val output =
                ProcessOutput.getProcessOutput(
                    ProcessOutput.STANDARD_TIMEOUT,
                    canonicalHome,
                    exePath,
                    "--short-version"
                )
            val version =
                if (output.exitCode == 0 && !output.isTimeout && !output.isCancelled) {
                    output.stdoutLines.firstOrNull { it.isNotBlank() }?.trim()
                } else {
                    LOG.debug("Failed to read Elixir version from $exePath (exitCode=${output.exitCode})")
                    elixirVersionFromHomePath(canonicalHome)
                }
            if (version != null) {
                versionByHomePath[cacheKey] = version
            }
            version
        } catch (e: Exception) {
            LOG.debug("Failed to read Elixir version from $exePath", e)
            elixirVersionFromHomePath(canonicalHome)
        }
    }

    private fun elixirVersionFromHomePath(homePath: String) =
        homePath.let { Release.fromString(File(it).name) }?.version()

    /**
     * Returns the bare canonical Elixir version string for [sdk] (e.g. `"1.15.7"`), or `null`
     * if the SDK is not an Elixir SDK or the version cannot be determined.
     *
     * The result is cached on the [sdk] instance via [UserDataHolderEx] after the first call.
     * On a cold start (after IDE restart, before any SDK setup has run), the backing
     * [ElixirVersionDetector.elixirVersion] may shell out to `elixir --short-version`.  That subprocess call
     * **must not** happen while a read lock is held - doing so blocks write actions and
     * triggers a platform SEVERE.  Always call this from a background thread or an IO
     * coroutine dispatcher, never from inside `readAction { }`.
     */
    @RequiresBackgroundThread
    fun canonicalVersion(sdk: Sdk): String? {
        ThreadingAssertions.assertBackgroundThread()

        if (sdk.sdkType !== Type.instance) return null

        sdk.getUserData(ELIXIR_VERSION_KEY)?.let { return it }

        // Cold path: may shell out to `elixir --short-version`.
        // Calling elixirVersion() under a read lock blocks write actions - assert early.
        check(!ApplicationManager.getApplication().holdsReadLock()) {
            "canonicalVersion() may shell out to `elixir --short-version` and must not be called under a read lock"
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

    /**
     * Returns the [Release] for [sdk], or `null` if the SDK is not an Elixir SDK or the
     * release cannot be determined.
     *
     * This method is read-lock-safe and may be called from any thread, including the EDT
     * and under a read action.  It uses only the pre-cached [ElixirVersionDetector.ELIXIR_VERSION_KEY] user data
     * (populated by a prior [ElixirVersionDetector.canonicalVersion] call from an IO phase) and falls back to
     * parsing the SDK home directory name.  It deliberately does **not** call
     * [ElixirVersionDetector.canonicalVersion] - that would spawn `elixir --short-version` on a cold start and
     * is forbidden under a read lock.
     *
     * Callers that need a guaranteed-accurate version at the cost of a subprocess on cold
     * start should call [ElixirVersionDetector.canonicalVersion] from a background/IO thread instead.
     */
    @Contract("null -> null")
    fun getRelease(sdk: Sdk?): Release? =
        if (sdk != null && sdk.sdkType === Type.instance) {
            // Prefer the pre-cached bare version (set by canonicalVersion() in IO phase).
            // Fall back to directory name parsing - never spawn a subprocess here.
            val cachedVersion = sdk.getUserData(ELIXIR_VERSION_KEY)
            cachedVersion?.let { Release.fromString(it) }
                ?: sdk.homePath?.let { Release.fromString(File(it).name) }
        } else {
            null
        }
}
