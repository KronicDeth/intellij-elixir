package org.elixir_lang.sdk.elixir

import com.ericsson.otp.erlang.OtpErlangBinary
import com.intellij.openapi.diagnostic.ControlFlowException
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.Key
import com.intellij.util.concurrency.ThreadingAssertions
import org.elixir_lang.beam.BeamReader
import org.elixir_lang.beam.ReadResult
import org.jetbrains.annotations.TestOnly
import java.io.File
import java.util.concurrent.CancellationException
import java.util.concurrent.ConcurrentHashMap
import org.elixir_lang.beam.chunk.code.operation.Code as OpCode
import org.elixir_lang.beam.term.Atom as BeamAtom
import org.elixir_lang.beam.term.List as BeamList
import org.elixir_lang.beam.term.Literal as BeamLiteral

/**
 * Reads build metadata directly from compiled BEAM artifacts in an Elixir SDK home.
 *
 * Currently used to extract which OTP release a given Elixir SDK was compiled against,
 * by parsing the otp_release key from the build_info/0 literal map in Elixir.System.beam.
 */
object ElixirBuildInfo {
    private val LOG = Logger.getInstance(ElixirBuildInfo::class.java)

    /**
     * The mtime in the key is what stops a rebuilt SDK at the same path being served a stale
     * major, and is why [org.elixir_lang.sdk.erlang.ErlangVersionDetector] keys its own the same way.
     */
    private val cache: ConcurrentHashMap<String, String> = ConcurrentHashMap()

    /** The cache is process-wide, so a test that needs a cold read has to clear it. */
    @TestOnly
    fun clearCache() {
        cache.clear()
    }

    /**
     * UserData key for caching the compiled-against OTP major on a [com.intellij.openapi.projectRoots.Sdk]
     * instance. E.g. `"26"`. Set during SDK registration; never re-computed from EDT.
     */
    val ELIXIR_OTP_MAJOR_KEY: Key<String> = Key.create("ELIXIR_OTP_MAJOR")

    /**
     * Extracts the compiled-against OTP release from `Elixir.System.beam`'s `build_info/0`
     * function literal map (the `otp_release` key).
     *
     * Returns the OTP major version string (e.g. `"26"`) or `null` if the BEAM file is
     * absent, cannot be parsed, or does not contain the expected map structure.
     *
     * Must be called with a canonical (WSL-resolved) home path.
     *
     * Must NOT be called on the EDT - reads a ~40KB BEAM file via [File.readBytes].
     * On WSL UNC paths (`\\wsl.localhost\...`) this goes through the Plan 9 filesystem
     * redirector and can take 50–200 ms. From a code path that may be invoked on the EDT,
     * launch this on a background coroutine. Do NOT reach for
     * [org.elixir_lang.util.runWithEdtGuard]: it moves the work off the EDT but does not drop read
     * access - see its KDoc.
     *
     * Two callers have not been moved yet and still wrap this in that helper -
     * `Type.suggestSdkName` and
     * `Type.versionStringForHome` - because both are synchronous `SdkType` work that must return a
     * value to the platform. They remain exposed to that failure; see issue #3955.
     */
    fun elixirOtpRelease(canonicalHome: String): String? {
        ThreadingAssertions.assertBackgroundThread()
        val beamFile = File(canonicalHome, "lib/elixir/ebin/Elixir.System.beam")
        val mtime = beamFile.lastModified()
        if (mtime == 0L) return null

        val cacheKey = "$canonicalHome@$mtime"
        // "" caches a definitive absence - a BEAM that parsed but carries no otp_release, i.e.
        // Elixir below 1.6. A FAILED read is not cached, because a failure that is not the file's
        // fault - a WSL redirector blip, a lock - leaves the bytes and the mtime alone, so the entry
        // would be served until something else edits the file.
        cache[cacheKey]?.let { return it.ifEmpty { null } }

        return when (val outcome = readOtpRelease(beamFile)) {
            is OtpRelease.Found -> outcome.major.also { cache[cacheKey] = it }
            OtpRelease.Absent -> null.also { cache[cacheKey] = "" }
            is OtpRelease.Unreadable -> {
                LOG.debug("Could not read otp_release from ${beamFile.path}: ${outcome.reason}", outcome.cause)
                null
            }
        }
    }

    private sealed interface OtpRelease {
        data class Found(val major: String) : OtpRelease

        /** The file parsed and carries no `otp_release`: Elixir below 1.6. */
        data object Absent : OtpRelease

        data class Unreadable(val reason: String, val cause: Throwable? = null) : OtpRelease
    }

    /**
     * A missing literal chunk is [OtpRelease.Absent], not [OtpRelease.Unreadable]: a module with no
     * literals has none, so there is no `otp_release` to find and nothing went wrong.
     */
    private fun readOtpRelease(beamFile: File): OtpRelease = try {
        when (val read = BeamReader.readResult(beamFile.readBytes(), beamFile.path, ::parseOtpRelease)) {
            null -> OtpRelease.Unreadable("not a BEAM")
            is ReadResult.Present -> read.value
            ReadResult.Absent -> OtpRelease.Absent
            is ReadResult.Unreadable -> OtpRelease.Unreadable(read.reason, read.cause)
        }
    } catch (exception: Exception) {
        // Reading the file. Errors are not caught, so a test-mode TestLoggerAssertionError still escapes and
        // fails the test.
        //
        // Hand-rolled for the same reason as [org.elixir_lang.beam.reportUnexpectedBeamData].
        if (exception is ControlFlowException || exception is CancellationException) throw exception
        OtpRelease.Unreadable("could not be read", exception)
    }

    /** A chunk the read cannot do without, so absent and unreadable both end it. */
    private inline fun <T : Any> ReadResult<T>.required(absent: String, otherwise: (OtpRelease) -> Nothing): T =
        when (this) {
            is ReadResult.Present -> value
            ReadResult.Absent -> otherwise(OtpRelease.Unreadable(absent))
            is ReadResult.Unreadable -> otherwise(OtpRelease.Unreadable(reason, cause))
        }

    private fun parseOtpRelease(reader: BeamReader): OtpRelease {
        val atoms = reader.atomsResult.required("no atom chunk") { return it }
        val code = reader.codeResult.required("no code chunk") { return it }
        val literals = when (val read = reader.literalsResult) {
            is ReadResult.Present -> read.value
            ReadResult.Absent -> return OtpRelease.Absent
            is ReadResult.Unreadable -> return OtpRelease.Unreadable(read.reason, read.cause)
        }

        val buildInfoIndex = (1..atoms.size()).firstOrNull { atoms.getOrNull(it)?.string == "build_info" }
            ?: return OtpRelease.Absent
        val otpReleaseIndex = (1..atoms.size()).firstOrNull { atoms.getOrNull(it)?.string == "otp_release" }
            ?: return OtpRelease.Absent

        var inBuildInfo = false
        for (i in 0 until code.size()) {
            val op = code[i]
            when (op.code) {
                OpCode.FUNC_INFO -> {
                    val func = op.termList.getOrNull(1) as? BeamAtom
                    val arity = op.termList.getOrNull(2) as? BeamLiteral
                    inBuildInfo = func?.index == buildInfoIndex && arity?.index == 0
                }
                OpCode.PUT_MAP_ASSOC, OpCode.PUT_MAP_EXACT -> {
                    if (!inBuildInfo) continue
                    val elements = (op.termList.getOrNull(4) as? BeamList)?.elements ?: continue
                    var j = 0
                    while (j < elements.size - 1) {
                        if ((elements[j] as? BeamAtom)?.index == otpReleaseIndex) {
                            val litIndex = (elements[j + 1] as? BeamLiteral)?.index ?: break
                            val major = (literals[litIndex] as? OtpErlangBinary)
                                ?.let { String(it.binaryValue(), Charsets.UTF_8).trim() }
                            // A blank literal is not a major, and caching it as Found would return
                            // "" once and null thereafter from the same bytes.
                            return major?.takeIf { it.isNotEmpty() }
                                ?.let { OtpRelease.Found(it) }
                                ?: OtpRelease.Absent
                        }
                        j += 2
                    }
                }
                else -> {}
            }
        }
        return OtpRelease.Absent
    }
}
