package org.elixir_lang.sdk.erlang

import com.intellij.openapi.diagnostic.Logger
import com.intellij.util.concurrency.ThreadingAssertions
import org.elixir_lang.sdk.wsl.wslCompat
import java.io.File
import java.util.concurrent.ConcurrentHashMap

/**
 * Detects the installed Erlang/OTP SDK version by reading the filesystem - no subprocess required.
 *
 * The canonical source of truth for an installed OTP release is:
 *   `<sdkHome>/releases/<N>/OTP_VERSION`
 * where `<N>` is the OTP major release directory (e.g. `26`).
 *
 * [detectRelease] must NOT be called on the EDT. WSL paths (\\wsl.localhost\...) involve
 * the Plan 9 filesystem redirector and directory/file access can take 50–200 ms.
 * Call only from background threads or [kotlinx.coroutines.Dispatchers.IO] contexts.
 * The result is stored on the SDK object at registration time; subsequent accesses read
 * the persisted value and do not call this function.
 */
object ErlangVersionDetector {
    private val LOGGER = Logger.getInstance(ErlangVersionDetector::class.java)

    // Keyed by "$canonicalHomePath@$otpVersionFileMtime" so entries for replaced OTP installs
    // are superseded automatically.
    private val cache: ConcurrentHashMap<String, Release> = ConcurrentHashMap()

    /**
     * Reads the installed Erlang/OTP version from `<sdkHome>/releases/<N>/OTP_VERSION`.
     *
     * Returns a [Release] with the OTP major and full patch version, or `null` if the
     * `releases/` directory or `OTP_VERSION` file is absent or unreadable.
     *
     * When Erlang is patched in place via `otp_patch_apply`, the version string is
     * suffixed with `**` (e.g. `"26.2.5.1**"`). This suffix is stripped before
     * constructing the [Release].
     *
     * Must NOT be called on the EDT - see class KDoc.
     */
    fun detectRelease(sdkHome: String): Release? {
        ThreadingAssertions.assertBackgroundThread()

        val canonicalHome = wslCompat.canonicalizePath(sdkHome)
        val releasesDir = File(canonicalHome, "releases")

        val otpMajorDir = if (!releasesDir.exists()) {
            LOGGER.warn("Can't detect Erlang version: ${releasesDir.path} is missing")
            return null
        } else {
            releasesDir.listFiles { f -> f.isDirectory && f.name.all { it.isDigit() } }
                ?.maxByOrNull { it.name.toIntOrNull() ?: 0 }
                ?: run {
                    LOGGER.debug("No numeric releases/ subdirectory found in $canonicalHome")
                    return null
                }
        }

        val otpVersionFile = File(otpMajorDir, "OTP_VERSION")
        val mtime = otpVersionFile.lastModified()
        if (mtime == 0L) {
            LOGGER.debug("OTP_VERSION file not found or unreadable: ${otpVersionFile.path}")
            return null
        }

        val cacheKey = "$canonicalHome@$mtime"
        // Fast path: return cached result if present.
        // Two threads with the same key can both miss and both compute - this is intentional.
        // The computation is a single file-read, the result is deterministic, and ConcurrentHashMap
        // guarantees the winning put is visible to all subsequent reads. Wrapping in computeIfAbsent
        // would prevent the double-compute but requires the entire fallible read to live inside the
        // lambda, where early returns are not possible (non-inline). The benign race is the simpler
        // and correct tradeoff here.
        cache[cacheKey]?.let { return it }

        val otpVersion = try {
            otpVersionFile.readText().trim().trimEnd('*')
        } catch (e: Exception) {
            LOGGER.warn("Could not read OTP_VERSION from ${otpVersionFile.path}", e)
            return null
        }

        if (otpVersion.isBlank()) {
            LOGGER.warn("OTP_VERSION file is empty: ${otpVersionFile.path}")
            return null
        }

        val otpMajor = otpMajorDir.name
        val release = Release(otpMajor, otpVersion)
        cache[cacheKey] = release
        LOGGER.debug("Detected Erlang release: $release (from ${otpVersionFile.path})")
        return release
    }
}
