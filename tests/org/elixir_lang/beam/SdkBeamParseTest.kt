package org.elixir_lang.beam

import org.elixir_lang.beam.chunk.CallDefinitions.Companion.macroNameAritySortedSetByMacro
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.File

/**
 * Data-driven exhaustive coverage for the BEAM chunk parser: one test per `.beam` file shipped by
 * the resolved Elixir and Erlang SDKs (each app's `ebin` directory). Each module the SDK-under-test
 * ships gets its own pass/fail, so a new Erlang/Elixir version's format/pattern issues surface here
 * (per-module) rather than in the wild.
 *
 * The parse path (`BeamReader` / atoms / call-definitions) is platform-free, so this is a plain
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
        val read = BeamReader.readResult(beamFile.readBytes(), beamFile.path) { reader ->
            macroNameAritySortedSetByMacro(reader)
            Triple(reader.atomsResult, reader.exportsResult, reader.localsResult)
        }
        assertNotNull("$label: not read as a BEAM", read)
        assertTrue("$label: $read", read is ReadResult.Present)

        val (atoms, exports, locals) = (read as ReadResult.Present).value
        assertFalse("$label: blank module name, got $atoms", atoms.valueOrNull?.moduleName().isNullOrEmpty())

        // Every call definition chunk a real module ships must read.
        assertFalse("$label: $exports", exports is ReadResult.Unreadable)
        assertFalse("$label: $locals", locals is ReadResult.Unreadable)
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
