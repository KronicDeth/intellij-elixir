package org.elixir_lang.beam.chunk.beam_documentation

import com.ericsson.otp.erlang.*
import junit.framework.TestCase

class ErlangHtmlRendererTest : TestCase() {

    // Helper to build {Tag, Attrs, Children} tuples
    private fun el(tag: String, children: Array<OtpErlangObject>, attrs: Array<OtpErlangObject> = emptyArray()) =
        OtpErlangTuple(arrayOf(OtpErlangAtom(tag), OtpErlangList(attrs), OtpErlangList(children)))

    private fun bin(s: String) = OtpErlangBinary(s.toByteArray())

    fun testParagraph() {
        val term = OtpErlangList(arrayOf(el("p", arrayOf(bin("Hello world.")))))
        assertEquals("Hello world.", ErlangHtmlRenderer.render(term))
    }

    fun testMultipleParagraphs() {
        val term = OtpErlangList(arrayOf(
            el("p", arrayOf(bin("First."))),
            el("p", arrayOf(bin("Second.")))
        ))
        assertEquals("First.\n\nSecond.", ErlangHtmlRenderer.render(term))
    }

    fun testInlineCode() {
        val term = OtpErlangList(arrayOf(
            el("p", arrayOf(bin("Returns "), el("code", arrayOf(bin("true"))), bin(".")))
        ))
        assertEquals("Returns `true`.", ErlangHtmlRenderer.render(term))
    }

    fun testPreCodeBlock() {
        val term = OtpErlangList(arrayOf(
            el("pre", arrayOf(el("code", arrayOf(bin("erf(X) = 2/sqrt(pi)")))))
        ))
        assertEquals("```\nerf(X) = 2/sqrt(pi)\n```", ErlangHtmlRenderer.render(term))
    }

    fun testCodeInsidePreNotBackticked() {
        // {pre, [], [{code, [], ["hello"]}]} should NOT produce backticks inside the code block
        val term = OtpErlangList(arrayOf(
            el("pre", arrayOf(el("code", arrayOf(bin("hello")))))
        ))
        val result = ErlangHtmlRenderer.render(term)!!
        assertFalse("Code inside pre should not have backticks", result.contains("`hello`"))
        assertTrue(result.contains("hello"))
    }

    fun testHeadings() {
        assertEquals("# Title", ErlangHtmlRenderer.render(OtpErlangList(arrayOf(el("h1", arrayOf(bin("Title")))))))
        assertEquals("## Sub", ErlangHtmlRenderer.render(OtpErlangList(arrayOf(el("h2", arrayOf(bin("Sub")))))))
        assertEquals("### H3", ErlangHtmlRenderer.render(OtpErlangList(arrayOf(el("h3", arrayOf(bin("H3")))))))
        assertEquals("#### H4", ErlangHtmlRenderer.render(OtpErlangList(arrayOf(el("h4", arrayOf(bin("H4")))))))
    }

    fun testLink() {
        val href = OtpErlangTuple(arrayOf(OtpErlangAtom("href"), bin("https://example.com")))
        val term = OtpErlangList(arrayOf(
            el("a", arrayOf(bin("click")), arrayOf(href))
        ))
        assertEquals("[click](https://example.com)", ErlangHtmlRenderer.render(term))
    }

    fun testUnorderedList() {
        val term = OtpErlangList(arrayOf(
            el("ul", arrayOf(
                el("li", arrayOf(bin("one"))),
                el("li", arrayOf(bin("two")))
            ))
        ))
        val result = ErlangHtmlRenderer.render(term)!!
        assertTrue(result.contains("- one"))
        assertTrue(result.contains("- two"))
    }

    fun testEmphasis() {
        val term = OtpErlangList(arrayOf(el("em", arrayOf(bin("italic")))))
        assertEquals("*italic*", ErlangHtmlRenderer.render(term))
    }

    fun testStrong() {
        val term = OtpErlangList(arrayOf(el("strong", arrayOf(bin("bold")))))
        assertEquals("**bold**", ErlangHtmlRenderer.render(term))
    }

    fun testEmphasisInsidePreSuppressed() {
        val term = OtpErlangList(arrayOf(
            el("pre", arrayOf(el("em", arrayOf(bin("text")))))
        ))
        val result = ErlangHtmlRenderer.render(term)!!
        assertFalse("em inside pre should not have asterisks around text", result.contains("*text*"))
    }

    fun testHr() {
        val term = OtpErlangList(arrayOf(el("hr", emptyArray())))
        assertTrue(ErlangHtmlRenderer.render(term)!!.contains("---"))
    }

    fun testBr() {
        val term = OtpErlangList(arrayOf(el("br", emptyArray())))
        assertEquals("", ErlangHtmlRenderer.render(term)?.trim())
    }

    fun testDlDtDd() {
        val term = OtpErlangList(arrayOf(
            el("dl", arrayOf(
                el("dt", arrayOf(bin("Term"))),
                el("dd", arrayOf(bin("Definition")))
            ))
        ))
        val result = ErlangHtmlRenderer.render(term)!!
        assertTrue(result.contains("**Term**"))
        assertTrue(result.contains("Definition"))
    }

    fun testBinaryPassthrough() {
        val binary = OtpErlangBinary("plain text".toByteArray())
        assertEquals("plain text", ErlangHtmlRenderer.render(binary))
    }

    fun testNullOnAtom() {
        assertNull(ErlangHtmlRenderer.render(OtpErlangAtom("foo")))
    }

    fun testCharlistInChildren() {
        // Erlang charlist [104, 105] = "hi"
        val charlist = OtpErlangList(arrayOf(OtpErlangLong(104), OtpErlangLong(105)))
        val term = OtpErlangList(arrayOf(el("p", arrayOf(charlist))))
        assertEquals("hi", ErlangHtmlRenderer.render(term))
    }

    fun testUnknownTagPassesThrough() {
        val term = OtpErlangList(arrayOf(el("span", arrayOf(bin("inner")))))
        assertEquals("inner", ErlangHtmlRenderer.render(term))
    }

    fun testRealWorldMathModuleDoc() {
        // Simulates the actual OTP 23 math module doc for erf/1:
        // [{p, [], ["Returns the error function of ", {code, [], ["X"]}, ", where:"]},
        //  {pre, [], [{code, [], ["erf(X) = 2/sqrt(pi)*integral from 0 to X of exp(-t*t) dt."]}]}]
        val term = OtpErlangList(arrayOf(
            el("p", arrayOf(
                bin("Returns the error function of "),
                el("code", arrayOf(bin("X"))),
                bin(", where:")
            )),
            el("pre", arrayOf(
                el("code", arrayOf(bin("erf(X) = 2/sqrt(pi)*integral from 0 to X of exp(-t*t) dt.")))
            ))
        ))
        val result = ErlangHtmlRenderer.render(term)!!
        assertTrue(result.contains("Returns the error function of `X`, where:"))
        assertTrue(result.contains("```\nerf(X) = 2/sqrt(pi)*integral from 0 to X of exp(-t*t) dt.\n```"))
        // No backticks inside the code block
        assertFalse(result.contains("```\n`erf"))
    }
}
