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
        releaseName: String,
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
        releaseName: String,
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
        releaseName: String,
        process: Process?,
        logger: Logger
    )

}

/**
 * Creates the appropriate platform-specific QuoterPlatform implementation.
 * @param platform The target platform
 * @param startEpmd false to let the release rely on an already-running epmd rather than starting one
 *                  out of its own bundled ERTS - see [Epmd]. Defaults to the historical behaviour.
 * @return Platform-specific implementation
 */
fun createQuoterPlatform(platform: Platform, startEpmd: Boolean = true): QuoterPlatform {
    return when (platform) {
        Platform.WINDOWS -> WindowsQuoterPlatform(startEpmd)
        Platform.POSIX -> PosixQuoterPlatform(startEpmd)
    }
}

/**
 * The node name used when no per-checkout name is supplied - the value this was hardcoded to before
 * the name became configurable, so a caller that does not pass one behaves exactly as before.
 */
const val DEFAULT_QUOTER_NODE_NAME = "intellij_elixir@127.0.0.1"

/**
 * Returns the standard environment variables for Quoter daemon.
 * Used by all platform implementations.
 *
 * [releaseName] is the distributed Erlang node name the daemon registers with the machine-wide epmd.
 * It has to differ per checkout: epmd allows one node per name, so a second checkout starting a quoter
 * under the same name gets "the name ... seems to be in use by another Erlang node" and exits 0, which
 * the build reports as the daemon dying immediately.
 *
 * [startEpmd] false adds `-start_epmd false`, so the release does not spawn an epmd from its own bundled
 * ERTS inside the checkout - see [Epmd]. Pass it only once an epmd is known to be answering. It covers
 * every release invocation, not just the daemon start, because `pid` and `stop` are RPC and so start a
 * node too.
 */
fun getReleaseEnvironment(
    releaseTmp: File?,
    releaseName: String = DEFAULT_QUOTER_NODE_NAME,
    startEpmd: Boolean = true
): Map<String, String> {
    return buildMap {
        put("RELEASE_COOKIE", "intellij_elixir")
        put("RELEASE_DISTRIBUTION", "name")
        put("RELEASE_NAME", releaseName)
        releaseTmp?.let { put("RELEASE_TMP", it.absolutePath) }

        if (!startEpmd) {
            // Appended rather than set: erlexec reads one ERL_AFLAGS, so overwriting an inherited value
            // would silently drop flags the developer or CI set.
            val inherited = System.getenv("ERL_AFLAGS")?.trim().orEmpty()
            put("ERL_AFLAGS", listOf(inherited, "-start_epmd false").filter { it.isNotEmpty() }.joinToString(" "))
        }
    }
}
