package org.elixir_lang.beam.chunk.beam_documentation

import com.ericsson.otp.erlang.*
import junit.framework.TestCase

class DocumentationTest : TestCase() {

    private fun bin(s: String) = OtpErlangBinary(s.toByteArray())

    /**
     * Builds a minimal docs_v1 tuple and serializes it to ETF bytes,
     * then verifies Documentation.from(ByteArray) can parse it.
     *
     * docs_v1 tuple structure:
     * {docs_v1, Anno, BeamLanguage, Format, ModuleDoc, Metadata, Docs}
     */
    private fun buildDocsV1Bytes(
        beamLanguage: String = "erlang",
        format: OtpErlangObject = bin("text/markdown"),
        moduleDoc: OtpErlangObject = OtpErlangMap(arrayOf(bin("en")), arrayOf(bin("Module doc."))),
        metadata: OtpErlangObject = OtpErlangMap(emptyArray(), emptyArray()),
        docs: OtpErlangList = OtpErlangList()
    ): ByteArray {
        val tuple = OtpErlangTuple(arrayOf(
            OtpErlangAtom("docs_v1"),
            OtpErlangList(),                  // anno
            OtpErlangAtom(beamLanguage),
            format,
            moduleDoc,
            metadata,
            docs
        ))
        val stream = OtpOutputStream()
        stream.write_any(tuple)
        // Prepend ETF version byte (131)
        val raw = stream.toByteArray()
        val result = ByteArray(raw.size + 1)
        result[0] = 131.toByte()
        System.arraycopy(raw, 0, result, 1, raw.size)
        return result
    }

    fun testParseValidDocsV1() {
        val bytes = buildDocsV1Bytes()
        val documentation = Documentation.from(bytes)
        assertNotNull(documentation)
        assertEquals("erlang", documentation!!.beamLanguage)
        assertEquals("text/markdown", documentation.format)
        assertEquals("Module doc.", documentation.moduleDocs?.englishDocs)
    }

    fun testParseElixirDocsV1() {
        val bytes = buildDocsV1Bytes(beamLanguage = "elixir")
        val documentation = Documentation.from(bytes)
        assertNotNull(documentation)
        assertEquals("elixir", documentation!!.beamLanguage)
    }

    fun testParseWithFunctionDocs() {
        // Build a documented function entry
        val kindNameArity = OtpErlangTuple(arrayOf(
            OtpErlangAtom("function"),
            OtpErlangAtom("hello"),
            OtpErlangLong(1)
        ))
        val docMap = OtpErlangMap(arrayOf(bin("en")), arrayOf(bin("Says hello.")))
        val documented = OtpErlangTuple(arrayOf(
            kindNameArity,
            OtpErlangList(),
            OtpErlangList(arrayOf(bin("hello/1"))),
            docMap,
            OtpErlangMap(emptyArray(), emptyArray())
        ))
        val docs = OtpErlangList(arrayOf(documented))

        val bytes = buildDocsV1Bytes(docs = docs)
        val documentation = Documentation.from(bytes)
        assertNotNull(documentation)
        assertNotNull(documentation!!.docs)
        val d = documentation.docs!!.documented("function", "hello", 1)
        assertNotNull(d)
        assertEquals("hello", d!!.name)
        assertEquals(1, d.arity)
        assertEquals(listOf("hello/1"), d.signatures)
    }

    fun testInvalidBytesReturnNull() {
        assertNull(Documentation.from(byteArrayOf(0, 1, 2, 3)))
    }

    fun testNonDocsV1TupleReturnNull() {
        val tuple = OtpErlangTuple(arrayOf(
            OtpErlangAtom("not_docs"),
            OtpErlangList()
        ))
        val stream = OtpOutputStream()
        stream.write_any(tuple)
        val raw = stream.toByteArray()
        val result = ByteArray(raw.size + 1)
        result[0] = 131.toByte()
        System.arraycopy(raw, 0, result, 1, raw.size)
        assertNull(Documentation.from(result))
    }
}
