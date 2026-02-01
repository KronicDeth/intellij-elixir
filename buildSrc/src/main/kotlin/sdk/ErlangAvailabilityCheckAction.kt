package sdk

import org.gradle.api.Action
import org.gradle.api.GradleException
import org.gradle.api.logging.Logging
import org.gradle.api.Task
import org.gradle.api.tasks.testing.Test
import java.io.IOException
import java.io.Serializable

/**
 * Verifies Erlang is available on PATH (or ERLANG_SDK_HOME) before running non-UI tests.
 */
class ErlangAvailabilityCheckAction : Action<Task>, Serializable {
    override fun execute(task: Task) {
        // UI tests resolve SDKs via ResolveElixirErlangSdksTask instead.
        if (task !is Test || task.name == "testUI") {
            return
        }
        val logger = Logging.getLogger(ErlangAvailabilityCheckAction::class.java)
        val erlangSdkHome = System.getenv("ERLANG_SDK_HOME")
        if (!erlangSdkHome.isNullOrBlank()) {
            logger.lifecycle("Using ERLANG_SDK_HOME from environment: $erlangSdkHome")
            return
        }

        val erlCommand = if (isWindows()) "erl.exe" else "erl"
        try {
            val process = ProcessBuilder(erlCommand, "-version")
                .redirectErrorStream(true)
                .start()
            val exitCode = process.waitFor()
            if (exitCode != 0) {
                throw GradleException(
                    """
                    |Erlang/OTP not found or failed to run.
                    |Tests require Erlang to be installed and on PATH.
                    |
                    |Options:
                    |  1. Download from: https://www.erlang.org/downloads
                    |  2. Install via Chocolatey: choco install erlang
                    |  3. Set ERLANG_SDK_HOME environment variable to your Erlang installation directory
                    |
                    |The jps-builder tests auto-detect Erlang by running '$erlCommand -eval' command.
                    """.trimMargin()
                )
            }
            logger.lifecycle("Erlang found: $erlCommand is available on PATH")
        } catch (e: IOException) {
            throw GradleException(
                """
                |Erlang/OTP executable '$erlCommand' not found on PATH.
                |Tests require Erlang to be installed and on PATH.
                |
                |Options:
                |  1. Download from: https://www.erlang.org/downloads
                |  2. Install via Chocolatey: choco install erlang
                |  3. Set ERLANG_SDK_HOME environment variable to your Erlang installation directory
                |
                |The jps-builder tests auto-detect Erlang by running '$erlCommand -eval' command.
                """.trimMargin(),
                e
            )
        }
    }
}
