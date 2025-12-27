package org.elixir_lang.beam

import org.elixir_lang.PlatformTestCase
import org.elixir_lang.beam.Beam.Companion.from
import org.elixir_lang.beam.chunk.CallDefinitions.Companion.macroNameAritySortedSetByMacro
import org.elixir_lang.psi.call.name.Function.DEF
import org.elixir_lang.psi.call.name.Function.DEFP
import org.junit.Assert
import java.io.BufferedInputStream
import java.io.DataInputStream
import java.io.FileInputStream

class BeamTest: PlatformTestCase() {
    private var ebinDirectory: String? = null

    fun testElixirModule() {
        val beam = beam("Elixir.Kernel")
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

    fun testErlangModule() {
        val beam = beam("elixir_interpolation")
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

    private fun beam(baseName: String): Beam? {
        val path = "$ebinDirectory$baseName.beam"
        val dataInputStream = DataInputStream(
            BufferedInputStream(
                FileInputStream(path)
            )
        )
        return from(dataInputStream, path)
    }


    override fun setUp() {
        super.setUp()
        val ebinDirectory = System.getenv("ELIXIR_EBIN_DIRECTORY")
        Assert.assertNotNull("ELIXIR_EBIN_DIRECTORY is not set", ebinDirectory)
        this.ebinDirectory = ebinDirectory
    }

    override fun tearDown() {
        super.tearDown()
        ebinDirectory = null
    }
}
