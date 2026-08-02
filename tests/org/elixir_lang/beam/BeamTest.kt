package org.elixir_lang.beam

import org.elixir_lang.PlatformTestCase
import org.elixir_lang.beam.Beam.Companion.from
import org.elixir_lang.beam.chunk.CallDefinitions.Companion.macroNameAritySortedSetByMacro
import org.elixir_lang.psi.call.name.Function.DEF
import org.elixir_lang.psi.call.name.Function.DEFP
import org.junit.Assert
import java.io.BufferedInputStream
import java.io.DataInputStream
import java.io.File
import java.io.FileInputStream

/**
 * Two kinds of coverage for the BEAM chunk parser:
 *
 *  1. **Specific / deterministic** - parse frozen `.beam` fixtures (checked into testData) from
 *     multiple Elixir versions and assert exact, known structure (module name, particular
 *     call-definitions with their macro/arity, and the name-completeness invariant). Version-stable
 *     because the inputs are frozen.
 *
 *  2. **Exhaustive / generic** - parse EVERY `.beam` shipped by the resolved Elixir and Erlang SDKs
 *     and assert only the version-independent invariants (parses without throwing, has a module
 *     name, call-definitions parse). This exercises the parser against real, current compiler output
 *     for the version under test, so a new Erlang/Elixir version's format/pattern issues surface
 *     here instead of in the wild.
 */
class BeamTest : PlatformTestCase() {

    /*
     * Specific tests against frozen fixtures (one per Elixir version).
     */

    fun testElixirKernel_1_13_4() = assertElixirKernel(fixtureDir("1.13.4"))
    fun testElixirKernel_1_19_5() = assertElixirKernel(fixtureDir("1.19.5"))

    fun testElixirInterpolation_1_13_4() = assertElixirInterpolation(fixtureDir("1.13.4"))
    fun testElixirInterpolation_1_19_5() = assertElixirInterpolation(fixtureDir("1.19.5"))

    private fun assertElixirKernel(ebinDirectory: File) {
        val beam = beamIn(ebinDirectory, "Elixir.Kernel")
        Assert.assertNotNull(beam)
        val atoms = beam!!.atoms()
        Assert.assertNotNull(atoms)
        Assert.assertEquals("Elixir.Kernel", atoms!!.moduleName())
        val callDefinitionCount: Int = beam.callDefinitionsList(atoms).map { it.size() }.sum()
        Assert.assertTrue("There are no callDefinitions", callDefinitionCount > 0)
        val macroNameAritySortedSetByMacro = macroNameAritySortedSetByMacro(beam, atoms)
        val macroNameArityCount: Int =
            macroNameAritySortedSetByMacro.map { (_, macroNameAritySortedSet) -> macroNameAritySortedSet.size }.sum()
        assertEquals("There are nameless callDefinitions", callDefinitionCount, macroNameArityCount)
        val nodes = macroNameAritySortedSetByMacro.flatMap { (_, macroNameAritySortedSet) ->
            macroNameAritySortedSet.filter { it.name == "node" }
        }.sorted()
        Assert.assertEquals(2, nodes.size)
        Assert.assertEquals(0, nodes[0].arity)
        Assert.assertEquals(1, nodes[1].arity)
    }

    private fun assertElixirInterpolation(ebinDirectory: File) {
        val beam = beamIn(ebinDirectory, "elixir_interpolation")
        Assert.assertNotNull(beam)
        val atoms = beam!!.atoms()
        Assert.assertNotNull(atoms)
        Assert.assertEquals("elixir_interpolation", atoms!!.moduleName())
        val callDefinitionCount: Int = beam.callDefinitionsList(atoms).map { it.size() }.sum()
        Assert.assertTrue("There are no callDefinitions", callDefinitionCount > 0)
        val macroNameAritySortedSetByMacro = macroNameAritySortedSetByMacro(beam, atoms)
        val macroNameArityCount: Int =
            macroNameAritySortedSetByMacro.map { (_, macroNameAritySortedSet) -> macroNameAritySortedSet.size }.sum()
        Assert.assertEquals("There are nameless callDefinitions", callDefinitionCount, macroNameArityCount)

        val extracts = macroNameAritySortedSetByMacro.flatMap { (_, macroNameAritySortedSet) ->
            macroNameAritySortedSet.filter { it.name == "extract" }
        }.sorted()
        Assert.assertEquals(2, extracts.size)

        val firstExtract = extracts[0]
        Assert.assertEquals(DEF, firstExtract.macro)
        Assert.assertEquals(6, firstExtract.arity)

        val secondExtract = extracts[1]
        Assert.assertEquals(DEFP, secondExtract.macro)
        Assert.assertEquals(8, secondExtract.arity)
    }

    /*
     * Helpers. (The exhaustive per-beam sweep over the resolved SDKs lives in SdkBeamParseTest.)
     */

    private fun fixtureDir(version: String): File =
        File("testData/org/elixir_lang/beam/parser/$version")

    private fun beamIn(ebinDirectory: File, baseName: String): Beam? =
        DataInputStream(BufferedInputStream(FileInputStream(File(ebinDirectory, "$baseName.beam"))))
            .use { from(it, File(ebinDirectory, "$baseName.beam").path) }
}
