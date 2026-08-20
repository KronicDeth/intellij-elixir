package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangAtom
import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangLong
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import junit.framework.TestCase

class MatchTest : TestCase() {

    /**
     * Erlang: `X = ok`
     * RHS is an atom (doBlock = false), so the match result should also have doBlock = false.
     */
    fun testSimpleMatchHasDoBlockFalse() {
        val line = OtpErlangLong(1)
        val match = tuple(
            OtpErlangAtom("match"), line,
            tuple(OtpErlangAtom("var"), line, OtpErlangAtom("X")),  // left: X
            tuple(OtpErlangAtom("atom"), line, OtpErlangAtom("ok")) // right: ok
        )

        val result = Match.toMacroStringDeclaredScope(match, Scope.EMPTY)

        assertEquals("x = :ok", result.macroString.string)
        assertFalse("Simple match should have doBlock = false", result.macroString.doBlock)
    }

    /**
     * Erlang: `Cs = [C || {0,C} <- Class]`
     * RHS is a list comprehension (doBlock = true), so the match result must propagate
     * doBlock = true. This ensures callers parenthesize the expression to prevent
     * `end do` syntax errors when the match appears before a `do` block.
     */
    fun testMatchWithDoBlockRhsPropagatesDoBlock() {
        val line = OtpErlangLong(1)

        // Build a comprehension as the RHS: `for c <- class do c end`
        // In Erlang abstract code: {lc, Line, {var,_,'C'}, [{generate, Line, {var,_,'C'}, {var,_,'Class'}}]}
        val comprehension = tuple(
            OtpErlangAtom("lc"), line,
            tuple(OtpErlangAtom("var"), line, OtpErlangAtom("C")),
            OtpErlangList(arrayOf<OtpErlangObject>(
                tuple(
                    OtpErlangAtom("generate"), line,
                    tuple(OtpErlangAtom("var"), line, OtpErlangAtom("C")),
                    tuple(OtpErlangAtom("var"), line, OtpErlangAtom("Class"))
                )
            ))
        )

        val match = tuple(
            OtpErlangAtom("match"), line,
            tuple(OtpErlangAtom("var"), line, OtpErlangAtom("Cs")),  // left: Cs
            comprehension                                             // right: for comprehension
        )

        val result = Match.toMacroStringDeclaredScope(match, Scope.EMPTY)

        assertTrue("Match with comprehension RHS should have doBlock = true", result.macroString.doBlock)
        assertTrue("Match string should contain '='", result.macroString.string.contains("="))
        assertTrue("Match string should contain 'for'", result.macroString.string.contains("for"))
    }

    /**
     * Verify that group() wraps a doBlock = true match in parentheses.
     */
    fun testGroupWrapsDoBlockMatch() {
        val line = OtpErlangLong(1)

        val comprehension = tuple(
            OtpErlangAtom("lc"), line,
            tuple(OtpErlangAtom("var"), line, OtpErlangAtom("C")),
            OtpErlangList(arrayOf<OtpErlangObject>(
                tuple(
                    OtpErlangAtom("generate"), line,
                    tuple(OtpErlangAtom("var"), line, OtpErlangAtom("C")),
                    tuple(OtpErlangAtom("var"), line, OtpErlangAtom("Class"))
                )
            ))
        )

        val match = tuple(
            OtpErlangAtom("match"), line,
            tuple(OtpErlangAtom("var"), line, OtpErlangAtom("Cs")),
            comprehension
        )

        val result = Match.toMacroStringDeclaredScope(match, Scope.EMPTY)
        val grouped = result.macroString.group()

        assertTrue("Grouped doBlock match should be wrapped in parens", grouped.string.startsWith("("))
        assertTrue("Grouped doBlock match should be wrapped in parens", grouped.string.endsWith(")"))
        assertFalse("Grouped result should have doBlock = false", grouped.doBlock)
    }

    private fun tuple(vararg elements: OtpErlangObject): OtpErlangTuple = OtpErlangTuple(elements)
}
