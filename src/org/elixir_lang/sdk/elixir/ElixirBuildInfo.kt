package org.elixir_lang.sdk.elixir

import com.ericsson.otp.erlang.OtpErlangBinary
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.Key
import com.intellij.util.concurrency.ThreadingAssertions
import org.elixir_lang.beam.Beam
import java.io.File
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
     * redirector and can take 50–200 ms. Use [org.elixir_lang.sdk.runWithEdtGuard] when
     * calling from code paths that may be invoked on the EDT (e.g. `suggestSdkName`).
     */
    fun elixirOtpRelease(canonicalHome: String): String? {
        ThreadingAssertions.assertBackgroundThread()
        return try {
            val beamFile = File(canonicalHome, "lib/elixir/ebin/Elixir.System.beam")
            if (!beamFile.exists()) return null
            val beam = Beam.from(beamFile.readBytes(), beamFile.path) ?: return null
            val atoms = beam.atoms() ?: return null
            val code = beam.code() ?: return null
            val literals = beam.literals() ?: return null

            val buildInfoIndex = (1..atoms.size()).firstOrNull { atoms.getOrNull(it)?.string == "build_info" } ?: return null
            val otpReleaseIndex = (1..atoms.size()).firstOrNull { atoms.getOrNull(it)?.string == "otp_release" } ?: return null

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
                                return (literals[litIndex] as? OtpErlangBinary)
                                    ?.let { String(it.binaryValue(), Charsets.UTF_8).trim() }
                            }
                            j += 2
                        }
                    }
                    else -> {}
                }
            }
            null
        } catch (e: Exception) {
            LOG.debug("Could not read otp_release from Elixir.System.beam in $canonicalHome", e)
            null
        }
    }
}
