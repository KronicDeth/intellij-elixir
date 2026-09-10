package org.elixir_lang.beam

import org.elixir_lang.PlatformTestCase
import org.junit.Assert

/**
 * A file whose header cannot be read must be rejected **silently**.
 *
 * The BEAM file type gates on the extension alone, so every file named `*.beam` reaches the decompiler
 * whether or not its bytes are BEAM. Empty and truncated ones are therefore ordinary input here, not corrupt
 * input: an editor scratch buffer, a placeholder resource shipped inside an unrelated plugin's jar, or
 * a file the compiler is part way through writing. Reporting an error for those blames the plugin for
 * something the user cannot act on, and the compiler case resolves itself on the next write.
 *
 * `Chunk.typeID` already returns `null` rather than throwing when there are too few bytes to read one,
 * so the rest of the contract is that the caller stays quiet about it.
 */
class HeaderTest : PlatformTestCase() {
    fun testEmptyFileIsRejectedSilently() = assertRejectedSilently(ByteArray(0), "Elixir.Empty.beam")

    fun testFileTooShortForAHeaderIsRejectedSilently() =
        assertRejectedSilently("FOR".toByteArray(), "Elixir.Truncated.beam")

    /**
     * The case the two above do not reach: four bytes ARE readable, so the header is compared and
     * found wrong. That is the placeholder-in-a-jar shape.
     */
    fun testANonBeamHeaderIsRejectedSilently() =
        assertRejectedSilently("JUNKjunkJUNK".toByteArray(), "Elixir.NotABeam.beam")

    /**
     * A file the compiler is part way through writing. `Chunk.typeID` returns null at EOF rather
     * than throwing, so this reaches the section comparison, not the catch beside it.
     */
    fun testAHalfWrittenBeamIsRejectedSilently() =
        assertRejectedSilently("FOR1".toByteArray() + ByteArray(4), "Elixir.HalfWritten.beam")

    /**
     * [PlatformTestCase.captureLoggedErrors] is what makes "logged nothing" assertable rather than
     * merely unobserved: its default action set rethrows, so an error logged here would fail the test
     * on the error itself. Capturing turns that into an assertion that can name what was logged.
     */
    private fun assertRejectedSilently(content: ByteArray, path: String) {
        val (read, errors) = captureLoggedErrors { BeamReader.read(content, path) { true } }

        Assert.assertNull("$path is not a BEAM", read)
        Assert.assertEquals("Rejecting it must not be reported as an error", emptyList<Any>(), errors)
    }
}
