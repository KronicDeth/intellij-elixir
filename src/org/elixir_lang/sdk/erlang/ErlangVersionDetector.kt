package org.elixir_lang.sdk.erlang

import com.intellij.execution.ExecutionException
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.platform.ide.progress.ModalTaskOwner
import com.intellij.platform.ide.progress.runWithModalProgressBlocking
import org.elixir_lang.cli.getExecutableFilepathWslSafe
import org.elixir_lang.jps.shared.cli.CliTool
import org.elixir_lang.sdk.ProcessOutput
import org.elixir_lang.sdk.wsl.wslCompat
import java.io.File
import java.util.Collections
import java.util.WeakHashMap

object ErlangVersionDetector {
    private const val OTP_RELEASE_PREFIX_LINE = "org.elixir_lang.sdk.erlang.Type OTP_RELEASE:"
    private const val ERTS_VERSION_PREFIX_LINE = "org.elixir_lang.sdk.erlang.Type ERTS_VERSION:"
    private const val PRINT_VERSION_INFO_EXPRESSION =
        "io:format(\"~n~s~n~s~n~s~n~s~n\",[" +
                "\"$OTP_RELEASE_PREFIX_LINE\"," +
                "erlang:system_info(otp_release)," +
                "\"$ERTS_VERSION_PREFIX_LINE\"," +
                "erlang:system_info(version)" +
                "]),erlang:halt()."

    private val LOGGER = Logger.getInstance(ErlangVersionDetector::class.java)
    // Weak keys so entries for old "$canonicalHome@$mtime" cache keys are collected after
    // the erl binary is updated. Synchronized because this object is shared across threads.
    private val releaseBySdkHome: MutableMap<String, Release> =
        Collections.synchronizedMap(WeakHashMap())

    fun detectSdkVersion(sdkHome: String): Release? {
        val canonicalHomePath = wslCompat.canonicalizePath(sdkHome)
        val cachedRelease = getVersionCacheKey(canonicalHomePath)?.let { releaseBySdkHome[it] }
        if (cachedRelease != null) {
            return cachedRelease
        }

        val erl = erlExecutable(canonicalHomePath)
        LOGGER.debug("=== ERLANG SDK: Erl executable path: ${erl.absolutePath}")
        if (!erl.canExecute()) {
            val message =
                buildString {
                    append("Can't detect Erlang version: ${erl.path}")
                    if (erl.exists()) append(" is not executable.") else append(" is missing.")
                }
            LOGGER.warn(message)
            return null
        }

        // runBlockingMaybeCancellable can be reached transitively in WSL/Eel process startup,
        // and that path is forbidden on EDT. Guard EDT callers with modal progress.
        val app = ApplicationManager.getApplication()
        if (app.isDispatchThread) {
            return runWithModalProgressBlocking(
                ModalTaskOwner.guess(),
                "Detecting Erlang SDK version..."
            ) {
                detectSdkVersionBackground(sdkHome, erl)
            }
        }

        return detectSdkVersionBackground(sdkHome, erl)
    }

    private fun detectSdkVersionBackground(sdkHome: String, erl: File): Release? {
        LOGGER.debug("=== ERLANG SDK: Executing erl to detect version")
        return try {
            val erlPath = erl.absolutePath
            LOGGER.debug("=== ERLANG SDK: Calling getProcessOutput with workDir: $sdkHome, exe: $erlPath")
            val output =
                ProcessOutput.getProcessOutput(
                    10 * 1000,
                    sdkHome,
                    erlPath,
                    "-noshell",
                    "-eval",
                    PRINT_VERSION_INFO_EXPRESSION,
                )

            if (output.exitCode == 0 && !output.isCancelled && !output.isTimeout) {
                parseSdkVersion(output.stdoutLines)?.also { detectedRelease ->
                    LOGGER.debug("=== ERLANG SDK: Detected release: ${detectedRelease.otpRelease}")
                    getVersionCacheKey(sdkHome)?.let { key ->
                        releaseBySdkHome[key] = detectedRelease
                    }
                }
            } else {
                LOGGER.warn(
                    "=== ERLANG SDK: Failed to detect Erlang version. Workdir: '$sdkHome' " +
                        "ErlPath: '$erlPath' Exit Code: ${output.exitCode}\nStdOut: ${output.stdout}\n" +
                        "StdErr: ${output.stderr}"
                )
                null
            }
        } catch (e: ExecutionException) {
            LOGGER.warn("=== ERLANG SDK: Exception during version detection", e)
            null
        }
    }

    private fun parseSdkVersion(printVersionInfoOutput: List<String>): Release? {
        var otpRelease: String? = null
        var ertsVersion: String? = null

        val iterator = printVersionInfoOutput.listIterator()
        while (iterator.hasNext()) {
            when (iterator.next()) {
                OTP_RELEASE_PREFIX_LINE -> if (iterator.hasNext()) otpRelease = iterator.next()
                ERTS_VERSION_PREFIX_LINE -> if (iterator.hasNext()) ertsVersion = iterator.next()
            }
        }

        return if (otpRelease != null && ertsVersion != null) Release(otpRelease, ertsVersion) else null
    }

    private fun getVersionCacheKey(sdkHome: String?): String? {
        val homePath = sdkHome ?: return null
        val canonicalHomePath = wslCompat.canonicalizePath(homePath)
        val erlPath = CliTool.ERL.getExecutableFilepathWslSafe(canonicalHomePath)
        val lastModified = File(erlPath).lastModified()
        if (lastModified == 0L) {
            return null
        }
        return "$canonicalHomePath@$lastModified"
    }

    private fun erlExecutable(sdkHome: String): File = File(CliTool.ERL.getExecutableFilepathWslSafe(sdkHome))
}
