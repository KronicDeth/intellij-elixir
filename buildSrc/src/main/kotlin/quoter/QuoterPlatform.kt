package quoter

import platform.Platform
import platform.posix.PosixQuoterPlatform
import platform.windows.WindowsQuoterPlatform
import org.gradle.api.logging.Logger
import org.gradle.process.ExecOperations
import java.io.File

/**
 * Platform-specific operations for managing the Quoter daemon lifecycle.
 */
interface QuoterPlatform {

    /**
     * Starts the Quoter daemon.
     * @param execOps Gradle exec operations
     * @param executable Path to the quoter executable
     * @param releaseTmp Optional temporary directory for release files
     * @param logger Logger for output
     * @return Process handle (null for POSIX daemons)
     */
    fun startDaemon(
        execOps: ExecOperations,
        executable: File,
        releaseTmp: File?,
        logger: Logger
    ): Process?

    /**
     * Checks if the daemon is running.
     * @param execOps Gradle exec operations
     * @param executable Path to the quoter executable
     * @param releaseTmp Optional temporary directory
     * @param process Process handle from startDaemon (Windows only)
     * @param logger Logger for output
     * @return Pair of (isRunning, pidOutput)
     */
    fun checkStatus(
        execOps: ExecOperations,
        executable: File,
        releaseTmp: File?,
        process: Process?,
        logger: Logger
    ): Pair<Boolean, String>

    /**
     * Stops the daemon gracefully.
     * @param execOps Gradle exec operations
     * @param executable Path to the quoter executable
     * @param releaseTmp Optional temporary directory
     * @param process Process handle from startDaemon (Windows only)
     * @param logger Logger for output
     */
    fun stopDaemon(
        execOps: ExecOperations,
        executable: File,
        releaseTmp: File?,
        process: Process?,
        logger: Logger
    )

}

/**
 * Creates the appropriate platform-specific QuoterPlatform implementation.
 * @param platform The target platform
 * @return Platform-specific implementation
 */
fun createQuoterPlatform(platform: Platform): QuoterPlatform {
    return when (platform) {
        Platform.WINDOWS -> WindowsQuoterPlatform()
        Platform.POSIX -> PosixQuoterPlatform()
    }
}

/**
 * Returns the standard environment variables for Quoter daemon.
 * Used by all platform implementations.
 */
fun getReleaseEnvironment(releaseTmp: File?): Map<String, String> {
    return buildMap {
        put("RELEASE_COOKIE", "intellij_elixir")
        put("RELEASE_DISTRIBUTION", "name")
        put("RELEASE_NAME", "intellij_elixir@127.0.0.1")
        releaseTmp?.let { put("RELEASE_TMP", it.absolutePath) }
    }
}
