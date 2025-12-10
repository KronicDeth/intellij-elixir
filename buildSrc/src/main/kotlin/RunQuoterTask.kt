import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import java.io.ByteArrayOutputStream
import javax.inject.Inject

abstract class RunQuoterTask : DefaultTask() {

    @get:Inject
    abstract val execOps: ExecOperations

    @get:InputFile
    abstract val executable: RegularFileProperty

    @get:Internal
    abstract val tmpDir: DirectoryProperty

    @TaskAction
    fun run() {
        val exePath = executable.get().asFile.absolutePath
        val releaseTmp = tmpDir.orNull?.asFile?.absolutePath
        val maxAttempts = 20

        logger.lifecycle("Starting Quoter daemon using: $exePath")

        // 1. Start the Daemon
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
            logger.error("Output:\n${startOutput}")
            throw GradleException("Quoter daemon failed to launch (Exit code ${startResult.exitValue})")
        }

        // 2. Poll for readiness
        logger.lifecycle("Waiting for Quoter daemon to start...")

        var isUp = false
        var lastPidOutput = ""

        // Loop 0 to 9
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

            lastPidOutput = pidStream.toString().trim()

            if (pidResult.exitValue == 0) {
                logger.lifecycle("Quoter daemon is UP! (PID: $lastPidOutput)")
                isUp = true
                return // Exit the run() method entirely on success
            }

            // ONLY sleep if this is NOT the last attempt
            if (attempt < maxAttempts - 1) {
                logger.lifecycle("Quoter daemon not ready yet (Attempt ${attempt + 1}/$maxAttempts). Retrying...")
            }
        }

        // If we reached here, the loop finished without returning
        logger.error("Quoter daemon failed to start after $maxAttempts attempts.")
        logger.error("Last 'pid' check output:\n$lastPidOutput")

        if (startOutput.size() > 0) {
            logger.error("Original Daemon Startup Output:\n$startOutput")
        }

        throw GradleException("Quoter daemon failed to start.")
    }
}
