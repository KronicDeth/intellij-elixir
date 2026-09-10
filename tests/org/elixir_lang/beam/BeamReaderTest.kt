package org.elixir_lang.beam

import com.intellij.testFramework.BinaryLightVirtualFile
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.beam.BeamBytes.beam
import org.elixir_lang.beam.BeamBytes.bytesOf
import org.elixir_lang.beam.BeamBytes.elixirSystemBeam
import org.elixir_lang.beam.BeamBytes.unsignedInt
import org.elixir_lang.beam.BeamBytes.withChunkField
import org.elixir_lang.beam.BeamBytes.withUndecodableLiteralTable
import org.elixir_lang.beam.chunk.Code
import org.junit.Assert

/** Reporting is decided once for a whole read, and the block's own work is contained with the chunks it reads. */
class BeamReaderTest : PlatformTestCase() {
    fun testAnUnreadableChunkIsReportedOncePerRead() {
        val (_, errors) = captureLoggedErrors { BeamReader.read(corruptAtomTable(), PATH) { listOf(it.atoms, it.atoms) } }

        Assert.assertEquals("got $errors", 1, errors.count { "$it".contains("AtU8") })
    }

    fun testATypedReadIsNotReported() {
        val (read, errors) = captureLoggedErrors { BeamReader.read(corruptAtomTable(), PATH) { it.atomsResult } }

        Assert.assertTrue("got $read", read is ReadResult.Unreadable)
        Assert.assertEquals(emptyList<Any>(), errors)
    }

    fun testAReadResultReportsNothing() {
        val (read, errors) = captureLoggedErrors { BeamReader.readResult(corruptAtomTable(), PATH) { it.atoms ?: NO_ATOMS } }

        Assert.assertEquals(ReadResult.Present(NO_ATOMS), read)
        Assert.assertEquals(emptyList<Any>(), errors)
    }

    /** A literal table that does not decode must not look like a module without one, which SDK detection caches. */
    fun testLiteralsThatDoNotDecodeAreUnreadableAndReportedByNoDecoder() {
        val (read, errors) = captureLoggedErrors {
            BeamReader.readResult(withUndecodableLiteralTable(elixirSystemBeam()), PATH) { it.literalsResult }
        }

        Assert.assertTrue("got $read", read is ReadResult.Present && read.value is ReadResult.Unreadable)
        Assert.assertEquals(emptyList<Any>(), errors)
    }

    /** The code still decodes; the warning is for a format change, and a read result reports nothing. */
    fun testAReadResultReportsNoFormatWarning() {
        val newerCode = withChunkField(elixirSystemBeam(), "Code", fieldIndex = 2, value = 0xFFFF)

        val (read, errors) = captureLoggedErrors { BeamReader.readResult(newerCode, PATH) { it.code } }

        Assert.assertTrue("got $read", read is ReadResult.Present)
        Assert.assertEquals(emptyList<Any>(), errors)
    }

    fun testWorkThatFailsOnDecodedDataIsReported() {
        val (value, errors) = captureLoggedErrors {
            BeamReader.read<String>(elixirSystemBeam(), PATH) { throw IllegalStateException(UNHANDLED) }
        }

        Assert.assertNull(value)
        Assert.assertTrue("got $errors", errors.any { "$it".contains(UNHANDLED) })
    }

    fun testWorkThatFailsInAReadResultSaysWhy() {
        val (read, errors) = captureLoggedErrors {
            BeamReader.readResult<String>(elixirSystemBeam(), PATH) { throw IllegalStateException(UNHANDLED) }
        }

        Assert.assertTrue("got $read", read is ReadResult.Unreadable && read.cause is IllegalStateException)
        Assert.assertEquals(emptyList<Any>(), errors)
    }

    /** The Code view asks again whenever its options change, and the same broken chunk is not news. */
    fun testAnAssemblyThatFailsIsReportedOncePerCachedReader() {
        val cached = CachedBeamReader.from(BinaryLightVirtualFile(PATH, beam("Code" to codeWithALabelThatIsNotALiteral())))!!

        val (assemblies, errors) = captureLoggedErrors {
            listOf(cached.assembly(Code.Options()), cached.assembly(Code.Options(showArgumentNames = false)))
        }

        Assert.assertEquals(listOf(null, null), assemblies)
        Assert.assertEquals("got $errors", 1, errors.count { "$it".contains("Code assembly") })
    }

    fun testBytesThatAreNotABeamAreNotRead() =
        Assert.assertNull(BeamReader.readResult("JUNKjunkJUNK".toByteArray(), PATH) { true })

    private fun corruptAtomTable(): ByteArray = withChunkField(elixirSystemBeam(), "AtU8", fieldIndex = 0, value = Int.MAX_VALUE)

    /**
     * `label` then `func_info`, so the label falls in a function body the assembly renders. Its argument is atom 1
     * where a literal belongs. The trailing `int_code_end` is never decoded: decoding stops before the last byte.
     */
    private fun codeWithALabelThatIsNotALiteral(): ByteArray =
        bytesOf(
            unsignedInt(16), unsignedInt(0), unsignedInt(3), unsignedInt(1), unsignedInt(1),
            LABEL, ATOM_1, FUNC_INFO, ATOM_1, ATOM_2, LITERAL_0, INT_CODE_END,
        )

    private companion object {
        const val PATH = "Elixir.Corrupt.beam"
        const val NO_ATOMS = "no atoms"
        const val UNHANDLED = "a shape nothing handles"

        const val LABEL = 1
        const val FUNC_INFO = 2
        const val INT_CODE_END = 3
        const val LITERAL_0 = 0x00
        const val ATOM_1 = 0x12
        const val ATOM_2 = 0x22
    }
}
