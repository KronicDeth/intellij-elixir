package platform.posix

import quoter.QuoterPlatform
import quoter.getReleaseEnvironment
import org.gradle.api.logging.Logger
import org.gradle.process.ExecOperations
import java.io.ByteArrayOutputStream
import java.io.File

/**
 * POSIX implementation of QuoterPlatform.
 * Uses the 'daemon' command which detaches the process automatically.
 */
class PosixQuoterPlatform : QuoterPlatform {

    override fun startDaemon(
        execOps: ExecOperations,
        executable: File,
        releaseTmp: File?,
        logger: Logger
    ): Process? {
        logger.lifecycle("Starting Quoter daemon (POSIX - detached)...")

        val startOutput = ByteArrayOutputStream()
        val result = execOps.exec {
            commandLine(executable.absolutePath, "daemon")
            environment(getReleaseEnvironment(releaseTmp))
            standardOutput = startOutput
            errorOutput = startOutput
            isIgnoreExitValue = true
        }

        if (result.exitValue != 0) {
            logger.error("Quoter daemon failed to launch")
            logger.error("Output:\n$startOutput")
            throw RuntimeException("Quoter daemon failed to launch (exit code ${result.exitValue})")
        }

        logger.lifecycle("Daemon start command completed")
        return null // POSIX daemon detaches, no process handle
    }

    override fun checkStatus(
        execOps: ExecOperations,
        executable: File,
        releaseTmp: File?,
        process: Process?,
        logger: Logger
    ): Pair<Boolean, String> {
        val pidOutput = ByteArrayOutputStream()
        val errorStream = ByteArrayOutputStream()
        val result = execOps.exec {
            commandLine(executable.absolutePath, "pid")
            environment(getReleaseEnvironment(releaseTmp))
            standardOutput = pidOutput
            errorOutput = errorStream
            isIgnoreExitValue = true
        }

        // Log stderr separately if present
        val stderr = errorStream.toString().trim()
        if (stderr.isNotEmpty()) {
            logger.debug("Quoter stderr: $stderr")
        }

        val output = pidOutput.toString().trim()
        return Pair(result.exitValue == 0, output)
    }

    override fun stopDaemon(
        execOps: ExecOperations,
        executable: File,
        releaseTmp: File?,
        process: Process?,
        logger: Logger
    ) {
        logger.lifecycle("Stopping Quoter daemon (POSIX)...")

        val stopOutput = ByteArrayOutputStream()
        val result = execOps.exec {
            commandLine(executable.absolutePath, "stop")
            environment(getReleaseEnvironment(releaseTmp))
            standardOutput = stopOutput
            errorOutput = stopOutput
            isIgnoreExitValue = true
        }

        if (result.exitValue == 0) {
            logger.lifecycle("Quoter daemon stopped gracefully")
        } else {
            logger.lifecycle("Quoter daemon was not running")
        }
    }
}
