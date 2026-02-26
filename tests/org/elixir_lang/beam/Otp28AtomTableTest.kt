package org.elixir_lang.beam

import org.elixir_lang.PlatformTestCase
import org.junit.Assert
import java.io.BufferedInputStream
import java.io.DataInputStream
import java.io.File
import java.io.FileInputStream

class Otp28AtomTableTest : PlatformTestCase() {
    override fun getTestDataPath(): String {
        return "testData/org/elixir_lang/beam/decompiler/OTP28"
    }

    fun testAtu8NegativeAtomCount() {
        val beamFile = File(testDataPath, "Elixir.Oban.beam")
        Assert.assertTrue("Missing test beam file at ${beamFile.absolutePath}", beamFile.exists())

        val beam = DataInputStream(
            BufferedInputStream(
                FileInputStream(beamFile)
            )
        ).use { dataInputStream ->
            Beam.from(dataInputStream, beamFile.path)
        }

        Assert.assertNotNull(beam)
        val atoms = beam!!.atoms()
        Assert.assertNotNull(atoms)
        Assert.assertEquals("Elixir.Oban", atoms!!.moduleName())
    }
}
