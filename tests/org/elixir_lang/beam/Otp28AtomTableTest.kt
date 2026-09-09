package org.elixir_lang.beam

import org.elixir_lang.PlatformTestCase
import org.junit.Assert
import java.io.File

class Otp28AtomTableTest : PlatformTestCase() {
    override fun getTestDataPath(): String {
        return "testData/org/elixir_lang/beam/decompiler/OTP28"
    }

    fun testAtu8NegativeAtomCount() {
        val beamFile = File(testDataPath, "Elixir.Oban.beam")
        Assert.assertTrue("Missing test beam file at ${beamFile.absolutePath}", beamFile.exists())

        val moduleName = BeamReader.read(beamFile.readBytes(), beamFile.path) { it.atoms?.moduleName() }

        Assert.assertEquals("Elixir.Oban", moduleName)
    }
}
