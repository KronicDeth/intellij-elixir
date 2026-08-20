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
     * Specific tests against frozen fixtures. Both halves of the directory name are load-bearing, for
     * different reasons, which is why it names a pair rather than either version alone:
     *
     *  - **OTP major** decides the *format*, and so which parser branch is exercised. The two
     *    directories differ on exactly the two OTP-24-vs-28 chunk changes, hitting both sentinel
     *    branches: a compressed `LitT` versus a zero-size sentinel, and `AtU8`. The major, not the patch
     *    - the format does not move within a major, and naming the patch would force a rename on every
     *    OTP bump.
     *  - **Elixir version** decides the asserted *content*. `extract/6` and `extract/8` below come from
     *    `lib/elixir/src/elixir_interpolation.erl` - Elixir's own source, merely compiled by the paired
     *    OTP's `erlc`. Those arities agree across 1.13.4 and 1.19.5, so the dependency is latent today,
     *    but a regenerated fixture from an Elixir that moved them would fail here with nothing in the
     *    name to say what it needed.
     *
     * So the name is *provenance* - what produced these bytes - not a coordinate in the CI matrix. It
     * does not go stale when `ci-versions.json` re-pairs an Elixir with a different OTP, which is
     * exactly what the old Elixir-only names did.
     */

    fun testElixirKernel_otp_24() = assertElixirKernel(fixtureDir("elixir-1.13.4-otp-24"))
    fun testElixirKernel_otp_28() = assertElixirKernel(fixtureDir("elixir-1.19.5-otp-28"))

    fun testElixirInterpolation_otp_24() = assertElixirInterpolation(fixtureDir("elixir-1.13.4-otp-24"))
    fun testElixirInterpolation_otp_28() = assertElixirInterpolation(fixtureDir("elixir-1.19.5-otp-28"))

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

    /**
     * A frozen-fixture directory, named `elixir-<elixir>-otp-<otpMajor>` after the pair that produced it.
     *
     * The OTP segment is the **major only**, deliberately unlike `sdk.pairToken`'s
     * `elixir-<elixir>-otp-<fullOtpVersion>`, so do not "align" the two: the chunk format changes on the
     * major, and carrying the patch would force a rename on every OTP bump. That is what made the old
     * Elixir-only names go stale when 1.19.5 moved from OTP 28.1 to 28.4 - they were a matrix coordinate,
     * and the matrix moved.
     */
    private fun fixtureDir(pair: String): File =
        File("testData/org/elixir_lang/beam/parser/$pair")

    private fun beamIn(ebinDirectory: File, baseName: String): Beam? =
        DataInputStream(BufferedInputStream(FileInputStream(File(ebinDirectory, "$baseName.beam"))))
            .use { from(it, File(ebinDirectory, "$baseName.beam").path) }
}
