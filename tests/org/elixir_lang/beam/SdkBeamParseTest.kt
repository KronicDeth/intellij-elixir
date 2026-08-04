package org.elixir_lang.beam

import org.elixir_lang.beam.Beam.Companion.from
import org.elixir_lang.beam.chunk.CallDefinitions.Companion.macroNameAritySortedSetByMacro
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.BufferedInputStream
import java.io.DataInputStream
import java.io.File
import java.io.FileInputStream

/**
 * Data-driven exhaustive coverage for the BEAM chunk parser: one test per `.beam` file shipped by
 * the resolved Elixir and Erlang SDKs (each app's `ebin` directory). Each module the SDK-under-test
 * ships gets its own pass/fail, so a new Erlang/Elixir version's format/pattern issues surface here
 * (per-module) rather than in the wild.
 *
 * The parse path (`Beam.from` / atoms / call-definitions) is platform-free, so this is a plain
 * JUnit 4 parameterized test rather than a PlatformTestCase - one lightweight instance per beam.
 * Version-specific *structural* assertions live in [BeamTest] against frozen fixtures.
 */
@RunWith(Parameterized::class)
class SdkBeamParseTest(
    @Suppress("unused") private val label: String,
    private val beamFile: File,
) {
    @Test
    fun parses() {
        val beam = DataInputStream(BufferedInputStream(FileInputStream(beamFile))).use { from(it, beamFile.path) }
        assertNotNull("$label: Beam.from returned null", beam)

        val atoms = beam!!.atoms()
        assertNotNull("$label: atoms() returned null", atoms)
        assertFalse("$label: blank module name", atoms!!.moduleName().isNullOrEmpty())

        // Must not throw for any real module the SDK ships.
        beam.callDefinitionsList(atoms)
        macroNameAritySortedSetByMacro(beam, atoms)
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun beams(): List<Array<Any>> {
            // ELIXIR_LANG_ELIXIR_PATH = resolved Elixir SDK root; ERLANG_SDK_HOME = resolved Erlang SDK.
            val params = (SdkBeams.forSdk(System.getenv("ELIXIR_LANG_ELIXIR_PATH"), "elixir") +
                    SdkBeams.forSdk(System.getenv("ERLANG_SDK_HOME"), "erlang"))
                .map { arrayOf<Any>(it.label, it.file) }
            require(params.isNotEmpty()) {
                "No .beam files found. Are ELIXIR_LANG_ELIXIR_PATH / ERLANG_SDK_HOME set to resolved SDKs?"
            }
            return params
        }
    }
}
