package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangAtom
import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangLong
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import junit.framework.TestCase

class ComprehensionTest : TestCase() {

    /**
     * Smoke-test: `lc` (list comprehension) is recognised.
     *
     * Erlang: `[X || X <- List]`
     * AST: `{lc, 1, {var,1,'X'}, [{generate,1,{var,1,'X'},{var,1,'List'}}]}`
     */
    fun testLcIsRecognised() {
        val lc = buildComprehension("lc",
            body = tuple(OtpErlangAtom("var"), line, OtpErlangAtom("X")),
            qualifiers = listOf(
                tuple(OtpErlangAtom("generate"), line,
                    tuple(OtpErlangAtom("var"), line, OtpErlangAtom("X")),
                    tuple(OtpErlangAtom("var"), line, OtpErlangAtom("List")))
            )
        )

        val result = Comprehension.ifToMacroStringDeclaredScope(lc, Scope.EMPTY)
        assertNotNull("lc should be recognised", result)
        assertTrue("lc result should contain 'for'", result!!.macroString.string.contains("for"))
    }

    /**
     * Smoke-test: `bc` (bitstring comprehension) is recognised.
     *
     * Erlang: `<< <<X>> || X <- List >>`
     */
    fun testBcIsRecognised() {
        val body = tuple(
            OtpErlangAtom("bin"), line,
            OtpErlangList(arrayOf<OtpErlangObject>(
                tuple(OtpErlangAtom("bin_element"), line,
                    tuple(OtpErlangAtom("var"), line, OtpErlangAtom("X")),
                    OtpErlangAtom("default"),
                    OtpErlangAtom("default"))
            ))
        )
        val bc = buildComprehension("bc",
            body = body,
            qualifiers = listOf(
                tuple(OtpErlangAtom("generate"), line,
                    tuple(OtpErlangAtom("var"), line, OtpErlangAtom("X")),
                    tuple(OtpErlangAtom("var"), line, OtpErlangAtom("List")))
            )
        )

        val result = Comprehension.ifToMacroStringDeclaredScope(bc, Scope.EMPTY)
        assertNotNull("bc should be recognised", result)
    }

    /**
     * Verifies that `mc` (OTP 26 map comprehension) is recognised and does not fall
     * through to the unknown-handler.
     *
     * Erlang: `#{K => V || K := V <- Map}`
     * AST: `{mc, 1, {map_field_assoc,1,{var,1,'K'},{var,1,'V'}},
     *              [{m_generate,1,{map_field_exact,1,{var,1,'K'},{var,1,'V'}},{var,1,'Map'}}]}`
     */
    fun testMcIsRecognised() {
        val body = tuple(
            OtpErlangAtom("map_field_assoc"), line,
            tuple(OtpErlangAtom("var"), line, OtpErlangAtom("K")),
            tuple(OtpErlangAtom("var"), line, OtpErlangAtom("V"))
        )
        val mc = buildComprehension("mc",
            body = body,
            qualifiers = listOf(
                tuple(OtpErlangAtom("m_generate"), line,
                    tuple(OtpErlangAtom("map_field_exact"), line,
                        tuple(OtpErlangAtom("var"), line, OtpErlangAtom("K")),
                        tuple(OtpErlangAtom("var"), line, OtpErlangAtom("V"))),
                    tuple(OtpErlangAtom("var"), line, OtpErlangAtom("Map")))
            )
        )

        val result = Comprehension.ifToMacroStringDeclaredScope(mc, Scope.EMPTY)
        assertNotNull("mc (OTP 26 map comprehension) should be recognised, not fall through to unknown", result)
        assertTrue("mc result should contain 'for'", result!!.macroString.string.contains("for"))
    }

    /**
     * Verifies that an unknown tag returns null.
     */
    fun testUnknownTagReturnsNull() {
        val unknown = buildComprehension("xc",
            body = tuple(OtpErlangAtom("var"), line, OtpErlangAtom("X")),
            qualifiers = emptyList()
        )

        val result = Comprehension.ifToMacroStringDeclaredScope(unknown, Scope.EMPTY)
        assertNull("Unknown comprehension tag should return null", result)
    }

    // ---- helpers ----

    private val line = OtpErlangLong(1)

    private fun buildComprehension(tag: String, body: OtpErlangTuple, qualifiers: List<OtpErlangTuple>): OtpErlangTuple =
        tuple(
            OtpErlangAtom(tag),
            line,
            body,
            OtpErlangList(qualifiers.toTypedArray<OtpErlangObject>())
        )

    private fun tuple(vararg elements: OtpErlangObject): OtpErlangTuple = OtpErlangTuple(elements)
}
