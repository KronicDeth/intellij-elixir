package org.elixir_lang.beam

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.newvfs.impl.VfsRootAccess
import com.intellij.openapi.progress.ProcessCanceledException
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.beam.BeamBytes.chunk
import org.elixir_lang.beam.BeamBytes.elixirSystemBeam
import org.elixir_lang.beam.BeamBytes.lineChunk
import org.elixir_lang.beam.BeamBytes.unsignedInt
import org.elixir_lang.beam.BeamBytes.withChunkField
import org.elixir_lang.beam.chunk.CallDefinitions
import org.elixir_lang.beam.chunk.Chunk
import org.elixir_lang.beam.chunk.Lines
import org.junit.Assert
import java.io.File
import java.util.concurrent.CancellationException

/**
 * The other half of [HeaderTest]'s contract: a file that IS a BEAM but carries data this decompiler
 * does not account for must still be reported.
 *
 * [reportUnexpectedBeamData] is loud in tests and quiet in production, so this asserts the loud
 * side. The production side is the platform's own [com.intellij.openapi.diagnostic.Logger.warnInProduction]
 * behaviour and is not re-tested here.
 */
class UnexpectedBeamDataTest : PlatformTestCase() {
    /** The VM refuses such a chunk as corrupt: after an incompatible change, its operations mean something else. */
    fun testAnUnknownCodeVersionIsUnreadable() {
        val tampered = withCodeChunkVersion(elixirSystemBeam(), version = 1)

        val code = BeamReader.read(tampered, "Elixir.System.beam") { it.codeResult }

        Assert.assertTrue("got $code", code is ReadResult.Unreadable && code.cause is RefusedBeamData)
        Assert.assertTrue("the refusal must name the version it did not expect, got $code", "$code".contains("Code version (1)"))
    }

    fun testAnOpcodeRangeBeyondWhatWeKnowIsReported() {
        // The field after the version. Only the declared maximum is rewritten - the instruction
        // bytes still name opcodes we know - so the report is the whole outcome here.
        val tampered = withChunkField(elixirSystemBeam(), "Code", fieldIndex = 2, value = 0xFFFF)

        val (code, errors) = captureLoggedErrors { BeamReader.read(tampered, "Elixir.System.beam") { it.code } }

        Assert.assertNotNull("only the max opcode was overwritten, so the file must still parse", code)
        Assert.assertEquals(
            "a wider opcode range than we know must be reported, got $errors",
            1,
            errors.size,
        )
        Assert.assertTrue(
            "the report must name the range it did not expect, got ${errors.single().message}",
            errors.single().message.contains("Max opcode (65535)"),
        )
    }

    /**
     * Cancellation must travel, not be logged. It reaches here only if a chunk parser ever catches
     * one and reports it, and wrapping it would hide it from `Logger`, whose guard reads the
     * throwable it is handed and not its cause.
     */
    fun testAControlFlowCauseIsRethrownRatherThanReported() =
        assertRethrown(ProcessCanceledException())

    /**
     * The clause the case above cannot reach: [ProcessCanceledException] is a `ControlFlowException`
     * as well as a `CancellationException`, and the check short-circuits on the first. Only a bare
     * cancellation exercises the second.
     */
    fun testABareCancellationCauseIsRethrownRatherThanReported() =
        assertRethrown(CancellationException())

    private fun assertRethrown(cause: Throwable) {
        val (thrown, errors) = captureLoggedErrors {
            try {
                Logger.getInstance(UnexpectedBeamDataTest::class.java)
                    .reportUnexpectedBeamData("should not be logged", cause)
                null
            } catch (caught: Throwable) {
                caught
            }
        }

        Assert.assertSame("the cause itself must come back out", cause, thrown)
        Assert.assertEquals("cancellation is not a report, got $errors", emptyList<Any>(), errors)
    }

    /**
     * A LitT header declaring more bytes than could plausibly be in it must be refused before the
     * allocation, not after: the field is an unsigned 32-bit int, so a corrupt one asks for up to
     * 2 GiB, and the resulting `OutOfMemoryError` is an `Error` that no caller here catches.
     */
    fun testAnImplausibleInflatedSizeIsRefusedBeforeAllocating() =
        assertRefused(declared = 1_500_000_000, named = "1500000000")

    /**
     * The top half of the field. `Chunk.unsignedInt` widens before shifting, so a leading byte at or
     * above 0x80 is a large positive rather than a negative - which the cap silently let through and
     * `ByteArray` then rejected with `NegativeArraySizeException`.
     */
    fun testAnInflatedSizeWithTheHighBitSetIsAlsoRefused() =
        assertRefused(declared = 3_000_000_000, named = "3000000000")

    private fun assertRefused(declared: Long, named: String) {
        val tampered = withLitTInflatedSize(elixirSystemBeam(), declared = declared)

        val literals = BeamReader.read(tampered, "Elixir.System.beam") { it.literalsResult }

        Assert.assertTrue(
            "a refusal must not look like a module with no literals, which callers cache; got $literals",
            literals is ReadResult.Unreadable && literals.cause is RefusedBeamData,
        )
        Assert.assertTrue("the declared size must be named, got $literals", "$literals".contains(named))
    }

    /** Rewrites the LitT chunk's uncompressed-size header, a big-endian unsigned 32-bit int. */
    private fun withLitTInflatedSize(content: ByteArray, declared: Long): ByteArray =
        withChunkField(content, "LitT", fieldIndex = 0, value = declared.toInt())

    /**
     * The chunk viewer reads literals through [CachedBeamReader], which reports a refusal and shows no
     * literals. Only the SDK path wants the distinction, and it uses [BeamReader.readResult].
     */
    fun testTheChunkViewerSeesARefusalAsNoLiterals() {
        val tampered = withLitTInflatedSize(elixirSystemBeam(), declared = 3_000_000_000)

        val (literals, errors) = captureLoggedErrors { CachedBeamReader.from(virtualFileOf(tampered))?.literals }

        Assert.assertNull("a refusal reaches the chunk viewer as no literals", literals)
        Assert.assertTrue(
            "the refusal must still be reported, got $errors",
            errors.any { it.message.contains("3000000000") },
        )
    }

    /** A chunk length past `Int.MAX_VALUE`, so one no array can hold. */
    fun testAChunkLengthBeyondIntIsRefused() {
        val header = "FOR1".toByteArray() + unsignedInt(12) + "BEAM".toByteArray()
        val chunk = "AtU8".toByteArray() + byteArrayOf(-1, -1, -1, -1)

        val (atoms, errors) = captureLoggedErrors { BeamReader.read(header + chunk, "Elixir.Huge.beam") { it.atomsResult } }

        Assert.assertTrue("the atom table runs past the end of the file, so it is cut off, not missing; got $atoms", atoms is ReadResult.Unreadable)
        Assert.assertEquals("refusing it is not an internal error, got $errors", emptyList<Any>(), errors)
    }

    fun testAWellFormedLineChunkParses() {
        // Atom 1 selects the file, then lines 5 and 7; one file name, "a.e".
        val lines = Lines.from(lineChunk(lineCount = 2, fileNameCount = 1, 0x12, 0x51, 0x71, 0, 3, 'a', '.', 'e'))

        Assert.assertEquals(listOf(1 to 5L, 1 to 7L), lines.lineReferenceList.map { it.fileNameIndex to it.line })
        Assert.assertEquals(listOf("Invalid", "a.e"), lines.fileNameList)
    }

    fun testALineChunkShorterThanItsHeaderIsRefused() =
        assertLinesRefused(chunk("Line", ByteArray(8)), named = "shorter than its 20 byte header")

    fun testALineChunkOfAnotherVersionIsRefused() =
        assertLinesRefused(lineChunk(lineCount = 0, fileNameCount = 0, version = 1), named = "version 1")

    fun testALineChunkWithFlagsIsRefused() =
        assertLinesRefused(lineChunk(lineCount = 0, fileNameCount = 0, flags = 1), named = "flags 1")

    fun testALineCountTheChunkCannotHoldIsRefused() =
        assertLinesRefused(lineChunk(lineCount = -1, fileNameCount = 0, 0x11), named = "Line references declares 4294967295")

    fun testAFileNameCountTheChunkCannotHoldIsRefused() =
        assertLinesRefused(lineChunk(lineCount = 0, fileNameCount = -1, 0, 1, 'a'), named = "Line file names declares 4294967295")

    /** Line items vary in size, so a count the bytes could hold can still run out of them. */
    fun testLineItemsRunningPastTheChunkAreRefused() =
        assertLinesRefused(lineChunk(lineCount = 2, fileNameCount = 0, 0x09, 0xFF), named = "Line references run past")

    fun testALineItemCutOffByTheChunkEndIsRefused() =
        assertLinesRefused(lineChunk(lineCount = 1, fileNameCount = 0, 0x09), named = "Line references run past")

    fun testALineItemTheDecoderRejectsIsRefused() =
        assertLinesRefused(lineChunk(lineCount = 1, fileNameCount = 0, 0x07), named = "undecodable")

    fun testALineItemThatIsNeitherAFileNorALineIsRefused() =
        assertLinesRefused(lineChunk(lineCount = 1, fileNameCount = 0, 0x03), named = "XRegister")

    fun testAFileNameRunningPastTheChunkIsRefused() =
        assertLinesRefused(lineChunk(lineCount = 0, fileNameCount = 1, 0, 5, 'a'), named = "Line file names run past")

    fun testAFileNameLengthRunningPastTheChunkIsRefused() =
        assertLinesRefused(lineChunk(lineCount = 0, fileNameCount = 2, 0, 2, 'a', 'b'), named = "Line file names run past")

    fun testAnExportCountTheChunkCannotHoldIsRefused() = assertCountRefused("ExpT") { it.exportsResult }
    fun testALocalCountTheChunkCannotHoldIsRefused() = assertCountRefused("LocT") { it.localsResult }
    fun testAnImportCountTheChunkCannotHoldIsRefused() = assertCountRefused("ImpT") { it.importsResult }

    fun testTheChunkViewerSeesARefusedLineChunkAsNoLines() =
        assertCachedAsNothing("Line", fieldIndex = 3, named = "Line references declares 4294967295") { it.lines }

    fun testTheChunkViewerSeesARefusedExportCountAsNoExports() =
        assertCachedAsNothing("ExpT", fieldIndex = 0, named = "ExpT declares 4294967295") { it.exports }

    fun testTheChunkViewerSeesARefusedLocalCountAsNoLocals() =
        assertCachedAsNothing("LocT", fieldIndex = 0, named = "LocT declares 4294967295") { it.locals }

    fun testTheChunkViewerSeesARefusedImportCountAsNoImports() =
        assertCachedAsNothing("ImpT", fieldIndex = 0, named = "ImpT declares 4294967295") { it.imports }

    /** The decompiler and the stub builder read call definitions through this, one chunk at a time. */
    fun testARefusedExportCountLeavesTheLocals() {
        val tampered = withChunkField(elixirSystemBeam(), "ExpT", fieldIndex = 0, value = -1)

        val (byMacro, errors) = captureLoggedErrors {
            BeamReader.read(tampered, "Elixir.System.beam") { CallDefinitions.macroNameAritySortedSetByMacro(it) }
        }

        Assert.assertNotNull("only the ExpT count was rewritten, so the file must still parse", byMacro)
        Assert.assertEquals("only the private definitions survive, got ${byMacro!!.keys}", setOf("defp"), byMacro.keys - "defmacrop")
        Assert.assertTrue("the refusal must still be reported, got $errors", errors.any { it.message.contains("ExpT declares 4294967295") })
    }

    private fun assertLinesRefused(chunk: Chunk, named: String) {
        val refused = try {
            Lines.from(chunk)
            null
        } catch (caught: RefusedBeamData) {
            caught
        }

        Assert.assertNotNull("a Line chunk that does not add up must be refused, not parsed as far as it goes", refused)
        Assert.assertTrue("the refusal must say $named, got ${refused?.message}", refused!!.message.orEmpty().contains(named))
    }

    private fun assertCountRefused(id: String, read: (BeamReader) -> ReadResult<*>) {
        val chunk = BeamReader.read(withChunkField(elixirSystemBeam(), id, fieldIndex = 0, value = -1), "Elixir.System.beam", read)

        Assert.assertTrue(
            "a count its chunk cannot hold must be refused, got $chunk",
            chunk is ReadResult.Unreadable && chunk.cause is RefusedBeamData,
        )
        Assert.assertTrue("the refusal must name the count, got $chunk", "$chunk".contains("$id declares 4294967295"))
    }

    private fun assertCachedAsNothing(id: String, fieldIndex: Int, named: String, read: (CachedBeamReader) -> Any?) {
        val cache = CachedBeamReader.from(virtualFileOf(withChunkField(elixirSystemBeam(), id, fieldIndex, value = -1)))
        Assert.assertNotNull("only the $id count was rewritten, so the file must still parse", cache)

        val (value, errors) = captureLoggedErrors { read(cache!!) }

        Assert.assertNull("a refusal reaches the chunk viewer as nothing, got $value", value)
        Assert.assertTrue("the refusal must still be reported, got $errors", errors.any { it.message.contains(named) })
    }

    /** The chunk viewer reaches [CachedBeamReader] through a file, so the refusal has to travel that way too. */
    private fun virtualFileOf(content: ByteArray): VirtualFile {
        val directory = FileUtil.createTempDirectory("refused_beam", null)
        Disposer.register(testRootDisposable) { FileUtil.delete(directory) }
        VfsRootAccess.allowRootAccess(testRootDisposable, directory.path)

        val file = File(directory, "Elixir.Refused.beam")
        file.writeBytes(content)

        return LocalFileSystem.getInstance().refreshAndFindFileByIoFile(file)!!
    }

    /** The `Code` chunk's fields are 0 the sub-size, 1 the version, 2 the max opcode. */
    private fun withCodeChunkVersion(content: ByteArray, version: Int): ByteArray =
        withChunkField(content, "Code", fieldIndex = 1, value = version)
}
