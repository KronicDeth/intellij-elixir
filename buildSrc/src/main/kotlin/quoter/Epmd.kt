package quoter

import org.gradle.api.logging.Logger
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * Keeps the machine-wide epmd out of the checkout.
 *
 * A distributed node starts epmd from the ERTS of whatever release is starting, detached, and the
 * quoter's release lives under `cache/` - so on Windows the leftover pins that directory and deleting
 * the checkout fails with "Device or resource busy". Only the first epmd survives (it is a singleton on
 * port 4369), so starting one from the SDK first makes the release's own attempt lose the race.
 */
object Epmd {
    private const val TIMEOUT_SECONDS = 10L

    /**
     * epmd ships in the ERTS directory, not in `<erlangHome>/bin` alongside `erl`, and the ERTS version
     * is not the OTP version - so it is found rather than derived. Where a tree holds more than one
     * ERTS any epmd will do; the sort is only for a stable choice, not a version comparison.
     */
    fun find(erlangHome: File): File? {
        val names = listOf("epmd.exe", "epmd")
        val ertsBins = (erlangHome.listFiles { file -> file.isDirectory && file.name.startsWith("erts-") } ?: emptyArray())
            .sortedBy { it.name }
            .map { File(it, "bin") }

        return (ertsBins + File(erlangHome, "bin"))
            .firstNotNullOfOrNull { bin -> names.map { File(bin, it) }.firstOrNull { it.isFile } }
    }

    /**
     * `epmd -daemon` is a no-op when the port is already bound, so this runs on every build regardless
     * of who started the one already there. The `-names` probe is what the result reports: suppressing
     * the release's own start on a false positive would leave it unable to bring up distribution.
     */
    fun ensureRunning(epmd: File, logger: Logger): Boolean = try {
        run(epmd, "-daemon")
        run(epmd, "-names").also { up ->
            logger.info(if (up) "epmd is up, from ${epmd.absolutePath}" else "epmd did not answer")
        }
    } catch (exception: Exception) {
        logger.info("Could not start epmd from ${epmd.absolutePath}: ${exception.message}")
        false
    }

    /**
     * Deliberately not `ExecOperations`: `-daemon` leaves a detached child holding the stdout handle it
     * inherited, so anything that reads the pipe to completion never returns - which hung a CI build for
     * the full 30-minute job timeout. Discarding both streams, and bounding the wait, is what makes this
     * safe to call on every build.
     */
    private fun run(epmd: File, argument: String): Boolean {
        val process = ProcessBuilder(epmd.absolutePath, argument)
            .redirectOutput(ProcessBuilder.Redirect.DISCARD)
            .redirectError(ProcessBuilder.Redirect.DISCARD)
            .start()

        if (!process.waitFor(TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
            process.destroyForcibly()
            return false
        }

        return process.exitValue() == 0
    }
}
