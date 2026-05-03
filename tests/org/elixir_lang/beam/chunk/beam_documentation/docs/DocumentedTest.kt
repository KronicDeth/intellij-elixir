package org.elixir_lang.beam.chunk.beam_documentation.docs

import com.ericsson.otp.erlang.*
import junit.framework.TestCase

class DocumentedTest : TestCase() {

    private fun bin(s: String) = OtpErlangBinary(s.toByteArray())
    private fun charlist(s: String) = OtpErlangList(s.map { OtpErlangLong(it.code.toLong()) }.toTypedArray())

    /**
     * Builds a minimal EEP-48 documented tuple:
     * {{Kind, Name, Arity}, Anno, Signatures, Doc, Metadata}
     */
    private fun documentedTuple(
        kind: String, name: String, arity: Int,
        signatures: OtpErlangList,
        doc: OtpErlangObject = OtpErlangAtom("none"),
        metadata: OtpErlangMap = OtpErlangMap(emptyArray(), emptyArray())
    ): OtpErlangTuple {
        val kindNameArity = OtpErlangTuple(arrayOf(
            OtpErlangAtom(kind),
            OtpErlangAtom(name),
            OtpErlangLong(arity.toLong())
        ))
        val anno = OtpErlangList() // empty annotation
        return OtpErlangTuple(arrayOf(kindNameArity, anno, signatures, doc, metadata))
    }

    fun testParseWithBinarySignatures() {
        val tuple = documentedTuple(
            "function", "any", 2,
            OtpErlangList(arrayOf(bin("any/2")))
        )
        val documented = Documented.from(tuple)
        assertNotNull(documented)
        assertEquals("function", documented!!.kind)
        assertEquals("any", documented.name)
        assertEquals(2, documented.arity)
        assertEquals(listOf("any/2"), documented.signatures)
    }

    fun testParseWithCharlistSignatures() {
        val tuple = documentedTuple(
            "function", "map", 2,
            OtpErlangList(arrayOf(charlist("map/2")))
        )
        val documented = Documented.from(tuple)
        assertNotNull(documented)
        assertEquals(listOf("map/2"), documented!!.signatures)
    }

    fun testParseWithEmptySignatures() {
        val tuple = documentedTuple(
            "function", "foo", 0,
            OtpErlangList()
        )
        val documented = Documented.from(tuple)
        assertNotNull(documented)
        assertTrue(documented!!.signatures.isEmpty())
    }

    fun testMetadataPreserved() {
        val metadata = OtpErlangMap(
            arrayOf(OtpErlangAtom("deprecated")),
            arrayOf(bin("Use bar/0 instead"))
        )
        val tuple = documentedTuple(
            "function", "foo", 0,
            OtpErlangList(arrayOf(bin("foo/0"))),
            metadata = metadata
        )
        val documented = Documented.from(tuple)
        assertNotNull(documented)
        assertNotNull(documented!!.deprecated())
    }

    fun testDocNoneAtom() {
        val tuple = documentedTuple(
            "function", "foo", 0,
            OtpErlangList(arrayOf(bin("foo/0"))),
            doc = OtpErlangAtom("none")
        )
        val documented = Documented.from(tuple)
        assertNotNull(documented)
        assertTrue(documented!!.doc is org.elixir_lang.beam.chunk.beam_documentation.docs.documented.None)
    }

    fun testDocHiddenAtom() {
        val tuple = documentedTuple(
            "function", "bar", 1,
            OtpErlangList(arrayOf(bin("bar/1"))),
            doc = OtpErlangAtom("hidden")
        )
        val documented = Documented.from(tuple)
        assertNotNull(documented)
        assertTrue(documented!!.doc is org.elixir_lang.beam.chunk.beam_documentation.docs.documented.Hidden)
    }

    fun testDocMarkdownMap() {
        val docMap = OtpErlangMap(
            arrayOf(bin("en")),
            arrayOf(bin("Does something useful."))
        )
        val tuple = documentedTuple(
            "function", "useful", 1,
            OtpErlangList(arrayOf(bin("useful/1"))),
            doc = docMap
        )
        val documented = Documented.from(tuple)
        assertNotNull(documented)
        assertTrue(documented!!.doc is org.elixir_lang.beam.chunk.beam_documentation.docs.documented.MarkdownByLanguage)
    }

    fun testNonTupleLogsErrorAndReturnsNull() {
        // DefaultLogger.error() throws AssertionError outside the full IDE test framework.
        // Catch it and verify the error message mentions the unrecognised input.
        val error = try {
            Documented.from(OtpErlangAtom("garbage"))
            null
        } catch (e: AssertionError) {
            e
        }
        assertNotNull("Expected Logger.error() to throw AssertionError for non-tuple input", error)
        assertTrue(
            "Error message should mention the unrecognised element, got: ${error!!.message}",
            error.message!!.contains("garbage")
        )
    }
}
