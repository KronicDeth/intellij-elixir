package org.elixir_lang.beam.chunk.beam_documentation

import com.ericsson.otp.erlang.*
import junit.framework.TestCase

class ModuleDocsTest : TestCase() {

    private fun bin(s: String) = OtpErlangBinary(s.toByteArray())
    private fun charlist(s: String) = OtpErlangList(s.map { OtpErlangLong(it.code.toLong()) }.toTypedArray())

    fun testBinaryKeyAndBinaryValue() {
        val map = OtpErlangMap(arrayOf(bin("en")), arrayOf(bin("Module docs")))
        val docs = ModuleDocs(map)
        assertEquals("Module docs", docs.englishDocs)
    }

    fun testCharlistKeyAndValue() {
        val map = OtpErlangMap(arrayOf(charlist("en")), arrayOf(charlist("Charlist docs")))
        val docs = ModuleDocs(map)
        assertEquals("Charlist docs", docs.englishDocs)
    }

    fun testErlangHtmlStructuredValue() {
        // application/erlang+html: [{p, [], ["Structured."]}]
        val structuredDoc = OtpErlangList(arrayOf(
            OtpErlangTuple(arrayOf(
                OtpErlangAtom("p"),
                OtpErlangList(),
                OtpErlangList(arrayOf(bin("Structured.")))
            ))
        ))
        val map = OtpErlangMap(arrayOf(bin("en")), arrayOf(structuredDoc))
        val docs = ModuleDocs(map)
        assertNotNull(docs.englishDocs)
        assertTrue(docs.englishDocs!!.contains("Structured."))
    }

    fun testNonEnglishLanguageIgnored() {
        val map = OtpErlangMap(arrayOf(bin("ja")), arrayOf(bin("Japanese only")))
        val docs = ModuleDocs(map)
        assertNull(docs.englishDocs)
    }

    fun testFromNullReturnsNull() {
        assertNull(ModuleDocs.from(null))
    }

    fun testFromNonNullReturnsModuleDocs() {
        val map = OtpErlangMap(arrayOf(bin("en")), arrayOf(bin("text")))
        val docs = ModuleDocs.from(map)
        assertNotNull(docs)
        assertEquals("text", docs!!.englishDocs)
    }
}
