package org.elixir_lang.beam.chunk.beam_documentation.docs.documented

import com.ericsson.otp.erlang.*
import junit.framework.TestCase

class DocTest : TestCase() {

    private fun bin(s: String) = OtpErlangBinary(s.toByteArray())
    private fun charlist(s: String) = OtpErlangList(s.map { OtpErlangLong(it.code.toLong()) }.toTypedArray())

    fun testNoneAtom() {
        val result = Doc.from(OtpErlangAtom("none"))
        assertTrue(result is None)
    }

    fun testHiddenAtom() {
        val result = Doc.from(OtpErlangAtom("hidden"))
        assertTrue(result is Hidden)
    }

    fun testUnknownAtomLogsErrorAndReturnsNull() {
        // DefaultLogger.error() throws AssertionError outside the full IDE test framework.
        // Catch it and verify the error message mentions the unrecognised atom.
        val error = try {
            Doc.from(OtpErlangAtom("something_else"))
            null
        } catch (e: AssertionError) {
            e
        }
        assertNotNull("Expected Logger.error() to throw AssertionError for unrecognised atom", error)
        assertTrue(
            "Error message should mention the unrecognised atom, got: ${error!!.message}",
            error.message!!.contains("something_else")
        )
    }

    fun testMarkdownByLanguageFromBinaryMap() {
        val map = OtpErlangMap(arrayOf(bin("en")), arrayOf(bin("Hello world")))
        val result = Doc.from(map)
        assertTrue(result is MarkdownByLanguage)
        assertEquals("Hello world", (result as MarkdownByLanguage).formattedByLanguage["en"])
    }

    fun testCharlistKeyAndBinaryValue() {
        val map = OtpErlangMap(arrayOf(charlist("en")), arrayOf(bin("docs here")))
        val result = Doc.from(map)
        assertTrue(result is MarkdownByLanguage)
        assertEquals("docs here", (result as MarkdownByLanguage).formattedByLanguage["en"])
    }

    fun testBinaryKeyAndCharlistValue() {
        val map = OtpErlangMap(arrayOf(bin("en")), arrayOf(charlist("charlist docs")))
        val result = Doc.from(map)
        assertTrue(result is MarkdownByLanguage)
        assertEquals("charlist docs", (result as MarkdownByLanguage).formattedByLanguage["en"])
    }

    fun testErlangHtmlStructuredValue() {
        // application/erlang+html format: [{p, [], ["text"]}]
        val structuredDoc = OtpErlangList(arrayOf(
            OtpErlangTuple(arrayOf(
                OtpErlangAtom("p"),
                OtpErlangList(),
                OtpErlangList(arrayOf(bin("Rendered text.")))
            ))
        ))
        val map = OtpErlangMap(arrayOf(bin("en")), arrayOf(structuredDoc))
        val result = Doc.from(map)
        assertTrue(result is MarkdownByLanguage)
        val text = (result as MarkdownByLanguage).formattedByLanguage["en"]
        assertNotNull(text)
        assertTrue("Should contain rendered text", text!!.contains("Rendered text."))
    }

    fun testMultipleLanguages() {
        val map = OtpErlangMap(
            arrayOf(bin("en"), bin("ja")),
            arrayOf(bin("English docs"), bin("Japanese docs"))
        )
        val result = Doc.from(map) as MarkdownByLanguage
        assertEquals("English docs", result.formattedByLanguage["en"])
        assertEquals("Japanese docs", result.formattedByLanguage["ja"])
    }
}
