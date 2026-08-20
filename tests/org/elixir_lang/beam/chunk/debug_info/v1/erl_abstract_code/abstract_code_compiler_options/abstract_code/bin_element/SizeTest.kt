package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.bin_element

import com.ericsson.otp.erlang.OtpErlangAtom
import com.ericsson.otp.erlang.OtpErlangLong
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import junit.framework.TestCase

class SizeTest : TestCase() {

    /**
     * Default size should return null (no size specifier to render).
     */
    fun testDefaultReturnsNull() {
        assertNull(Size.toString(OtpErlangAtom("default")))
    }

    /**
     * An integer literal size like `{integer, 1, 8}` should render as just `"8"`,
     * not `"size(8)"`, since Elixir accepts bare integer sizes in bitstrings.
     */
    fun testIntegerLiteralSizeRendersWithoutWrapper() {
        val size = tuple(OtpErlangAtom("integer"), OtpErlangLong(1), OtpErlangLong(8))

        assertEquals("8", Size.toString(size as OtpErlangObject))
    }

    /**
     * A variable size like `{var, 1, 'Padding'}` must render as `"size(padding)"`,
     * not bare `"padding"`, because Elixir would interpret `0 :: padding` as a
     * type specifier rather than a size.
     */
    fun testVariableSizeRendersWithSizeWrapper() {
        val size = tuple(OtpErlangAtom("var"), OtpErlangLong(1), OtpErlangAtom("Padding"))

        val result = Size.toString(size as OtpErlangObject)

        assertNotNull(result)
        assertTrue("Variable size should be wrapped with size(), got: $result", result!!.startsWith("size("))
        assertTrue("Variable size should end with ), got: $result", result.endsWith(")"))
    }

    /**
     * An expression size like `{op, 1, '*', {integer,1,8}, {var,1,'N'}}` must also
     * be wrapped with `size()`.
     */
    fun testExpressionSizeRendersWithSizeWrapper() {
        val size = tuple(
            OtpErlangAtom("op"), OtpErlangLong(1), OtpErlangAtom("*"),
            tuple(OtpErlangAtom("integer"), OtpErlangLong(1), OtpErlangLong(8)),
            tuple(OtpErlangAtom("var"), OtpErlangLong(1), OtpErlangAtom("N"))
        )

        val result = Size.toString(size as OtpErlangObject)

        assertNotNull(result)
        assertTrue("Expression size should be wrapped with size(), got: $result", result!!.startsWith("size("))
    }

    private fun tuple(vararg elements: OtpErlangObject): OtpErlangTuple = OtpErlangTuple(elements)
}
