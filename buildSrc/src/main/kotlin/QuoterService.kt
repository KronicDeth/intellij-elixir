import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.logging.Logging
import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters
import org.gradle.process.ExecOperations
import java.io.ByteArrayOutputStream
import javax.inject.Inject

/**
 * A BuildService that manages the Quoter daemon lifecycle.
 * The daemon is started on first use and automatically stopped when the build ends,
 * regardless of whether the build succeeded or failed.
 *
 * See: https://github.com/gradle/gradle/issues/27707
 */
abstract class QuoterService : BuildService<QuoterService.Params>, AutoCloseable {

    interface Params : BuildServiceParameters {
        val executable: RegularFileProperty
        val tmpDir: DirectoryProperty
    }

    @get:Inject
    abstract val execOps: ExecOperations

    private val logger = Logging.getLogger(QuoterService::class.java)

    @Volatile
    private var started = false

    fun ensureStarted() {
        if (started) return
        synchronized(this) {
            if (started) return
            startDaemon()
            started = true
        }
    }

    private fun startDaemon() {
        val exePath = parameters.executable.get().asFile.absolutePath
        val releaseTmp = parameters.tmpDir.orNull?.asFile?.absolutePath
        val maxAttempts = 20

        logger.lifecycle("Starting Quoter daemon using: $exePath")

        val startOutput = ByteArrayOutputStream()
        val startResult = execOps.exec {
            commandLine(exePath, "daemon")
            environment("RELEASE_COOKIE", "intellij_elixir")
            environment("RELEASE_DISTRIBUTION", "name")
            environment("RELEASE_NAME", "intellij_elixir@127.0.0.1")
            releaseTmp?.let { environment("RELEASE_TMP", it) }
            standardOutput = startOutput
            errorOutput = startOutput
            isIgnoreExitValue = true
        }

        if (startResult.exitValue != 0) {
            logger.error("Quoter daemon failed to launch immediately.")
            logger.error("Output:\n$startOutput")
            throw RuntimeException("Quoter daemon failed to launch (Exit code ${startResult.exitValue})")
        }

        logger.lifecycle("Waiting for Quoter daemon to start...")

        repeat(maxAttempts) { attempt ->
            Thread.sleep(1000)
            val pidStream = ByteArrayOutputStream()

            val pidResult = execOps.exec {
                commandLine(exePath, "pid")
                environment("RELEASE_COOKIE", "intellij_elixir")
                environment("RELEASE_DISTRIBUTION", "name")
                environment("RELEASE_NAME", "intellij_elixir@127.0.0.1")
                releaseTmp?.let { environment("RELEASE_TMP", it) }
                standardOutput = pidStream
                errorOutput = pidStream
                isIgnoreExitValue = true
            }

            val lastPidOutput = pidStream.toString().trim()

            if (pidResult.exitValue == 0) {
                logger.lifecycle("Quoter daemon is UP! (PID: $lastPidOutput)")
                return
            }

            if (attempt < maxAttempts - 1) {
                logger.lifecycle("Quoter daemon not ready yet (Attempt ${attempt + 1}/$maxAttempts). Retrying...")
            }
        }

        throw RuntimeException("Quoter daemon failed to start after $maxAttempts attempts.")
    }

    override fun close() {
        if (!started) return

        val exePath = parameters.executable.get().asFile.absolutePath
        val releaseTmp = parameters.tmpDir.orNull?.asFile?.absolutePath

        logger.lifecycle("Stopping Quoter daemon...")

        val stopOutput = ByteArrayOutputStream()
        val result = execOps.exec {
            commandLine(exePath, "stop")
            environment("RELEASE_COOKIE", "intellij_elixir")
            environment("RELEASE_DISTRIBUTION", "name")
            environment("RELEASE_NAME", "intellij_elixir@127.0.0.1")
            releaseTmp?.let { environment("RELEASE_TMP", it) }
            standardOutput = stopOutput
            errorOutput = stopOutput
            isIgnoreExitValue = true
        }

        if (result.exitValue == 0) {
            logger.lifecycle("Stopped Quoter daemon.")
        } else {
            logger.lifecycle("Quoter daemon was not running.")
        }
    }
}
