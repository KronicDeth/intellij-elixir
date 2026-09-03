package org.elixir_lang.beam

import org.elixir_lang.PlatformTestCase
import org.junit.Assert

/**
 * A file whose header cannot be read must be rejected **silently**.
 *
 * `Beam.is` gates on the extension alone, so every file named `*.beam` reaches the decompiler whether
 * or not its bytes are BEAM. Empty and truncated ones are therefore ordinary input here, not corrupt
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
     * [PlatformTestCase.captureLoggedErrors] is what makes "logged nothing" assertable rather than
     * merely unobserved: its default action set rethrows, so an error logged here would fail the test
     * on the error itself. Capturing turns that into an assertion that can name what was logged.
     */
    private fun assertRejectedSilently(content: ByteArray, path: String) {
        val (beam, errors) = captureLoggedErrors { Beam.from(content, path) }

        Assert.assertNull("A file too short to hold a header is not a BEAM", beam)
        Assert.assertEquals("Rejecting it must not be reported as an error", emptyList<Any>(), errors)
    }
}
