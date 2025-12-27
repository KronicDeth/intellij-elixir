package quoter

import elixir.ElixirService
import platform.detectPlatform
import platform.logPlatformDetection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.logging.Logging
import org.gradle.api.provider.Property
import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters
import org.gradle.process.ExecOperations
import javax.inject.Inject

/**
 * BuildService that manages the Quoter daemon lifecycle.
 * The daemon is started on first use and automatically stopped when the build ends,
 * regardless of whether the build succeeded or failed.
 *
 * Platform differences:
 * - POSIX: Uses 'daemon' command which detaches automatically
 * - Windows: Uses 'start' command with process management via ProcessBuilder
 *
 * See: https://github.com/gradle/gradle/issues/27707
 */
abstract class QuoterService : BuildService<QuoterService.Params>, AutoCloseable {

    interface Params : BuildServiceParameters {
        /** Path to the quoter executable */
        val executable: RegularFileProperty

        /** Temporary directory for quoter runtime files */
        val tmpDir: DirectoryProperty

        /** Reference to ElixirService for Mix commands (future use) */
        val elixirService: Property<ElixirService>
    }

    @get:Inject
    abstract val execOps: ExecOperations

    private val logger = Logging.getLogger(QuoterService::class.java)
    private val platform = detectPlatform()
    private val quoterPlatform = createQuoterPlatform(platform)

    @Volatile
    private var started = false

    @Volatile
    private var process: Process? = null

    /**
     * Ensures the Quoter daemon is started and ready.
     * Safe to call multiple times - only starts once.
     */
    fun ensureStarted() {
        if (started) return
        synchronized(this) {
            if (started) return
            startDaemon()
            started = true
        }
    }

    private fun startDaemon() {
        val executable = parameters.executable.get().asFile
        val releaseTmp = parameters.tmpDir.orNull?.asFile
        val maxAttempts = 20

        logPlatformDetection(logger)
        logger.lifecycle("Starting Quoter daemon: ${executable.absolutePath}")

        // Start the daemon (platform-specific)
        process = quoterPlatform.startDaemon(execOps, executable, releaseTmp, logger)

        // Wait for daemon to be ready (both platforms use RPC 'pid' command)
        logger.lifecycle("Waiting for Quoter daemon to be ready...")

        repeat(maxAttempts) { attempt ->
            Thread.sleep(1000)

            val (isRunning, pidOutput) = quoterPlatform.checkStatus(
                execOps, executable, releaseTmp, process, logger
            )

            if (isRunning) {
                logger.lifecycle("Quoter daemon is UP! (PID: ${pidOutput.trim()})")
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

        val executable = parameters.executable.get().asFile
        val releaseTmp = parameters.tmpDir.orNull?.asFile

        logger.lifecycle("Shutting down Quoter daemon...")

        quoterPlatform.stopDaemon(execOps, executable, releaseTmp, process, logger)

        logger.lifecycle("Quoter daemon shutdown complete")
    }
}
