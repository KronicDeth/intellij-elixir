package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.comprehension.qualifier

import com.ericsson.otp.erlang.OtpErlangAtom
import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangLong
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import junit.framework.TestCase
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Scope

class BitstringGenerateTest : TestCase() {

    /**
     * Erlang: `<<C:6>> <= <<Bin/binary>>`
     *
     * When the generator expression is a bitstring, the result must parenthesize
     * it to avoid ambiguous `>>>>` (which Elixir parses as `>>>` operator + `>`).
     * Expected: `<<c :: 6 <- (<<bin :: binary>>)>>`
     */
    fun testBitstringExpressionIsParenthesized() {
        val line = OtpErlangLong(1)

        // Pattern: {bin, 1, [{bin_element, 1, {var,1,'C'}, {integer,1,6}, default}]}
        val pattern = tuple(
            OtpErlangAtom("bin"), line,
            OtpErlangList(arrayOf<OtpErlangObject>(
                tuple(
                    OtpErlangAtom("bin_element"), line,
                    tuple(OtpErlangAtom("var"), line, OtpErlangAtom("C")),
                    tuple(OtpErlangAtom("integer"), line, OtpErlangLong(6)),
                    OtpErlangAtom("default")
                )
            ))
        )

        // Expression: {bin, 1, [{bin_element, 1, {var,1,'Bin'}, default, [binary]}]}
        val expression = tuple(
            OtpErlangAtom("bin"), line,
            OtpErlangList(arrayOf<OtpErlangObject>(
                tuple(
                    OtpErlangAtom("bin_element"), line,
                    tuple(OtpErlangAtom("var"), line, OtpErlangAtom("Bin")),
                    OtpErlangAtom("default"),
                    OtpErlangList(arrayOf<OtpErlangObject>(OtpErlangAtom("binary")))
                )
            ))
        )

        // b_generate: {b_generate, 1, Pattern, Expression}
        val bGenerate = tuple(OtpErlangAtom("b_generate"), line, pattern, expression)

        val result = BitstringGenerate.toMacroStringDeclaredScope(bGenerate, Scope.EMPTY)
        val string = result.macroString.string

        // The inner <<...>> must be wrapped in parens
        assertTrue(
            "Bitstring expression should be parenthesized to avoid >>>>, got: $string",
            string.contains("<- (<<")
        )
        // Must end with >>)>> not >>>>
        assertFalse(
            "Result should not contain ambiguous >>>>, got: $string",
            string.contains(">>>>")
        )
    }

    /**
     * Erlang: `<<C:6>> <= Var`
     *
     * When the generator expression is a simple variable (not a bitstring),
     * no extra parentheses are needed.
     */
    fun testNonBitstringExpressionIsNotParenthesized() {
        val line = OtpErlangLong(1)

        val pattern = tuple(
            OtpErlangAtom("bin"), line,
            OtpErlangList(arrayOf<OtpErlangObject>(
                tuple(
                    OtpErlangAtom("bin_element"), line,
                    tuple(OtpErlangAtom("var"), line, OtpErlangAtom("C")),
                    tuple(OtpErlangAtom("integer"), line, OtpErlangLong(6)),
                    OtpErlangAtom("default")
                )
            ))
        )

        // Expression is just a variable: {var, 1, 'Data'}
        val expression = tuple(OtpErlangAtom("var"), line, OtpErlangAtom("Data"))

        val bGenerate = tuple(OtpErlangAtom("b_generate"), line, pattern, expression)

        val result = BitstringGenerate.toMacroStringDeclaredScope(bGenerate, Scope.EMPTY)
        val string = result.macroString.string

        // Should contain `<- data>>` without extra parens
        assertTrue(
            "Non-bitstring expression should not be parenthesized, got: $string",
            string.contains("<- data>>")
        )
        assertFalse(
            "Non-bitstring expression should not have parens, got: $string",
            string.contains("<- (")
        )
    }

    private fun tuple(vararg elements: OtpErlangObject): OtpErlangTuple = OtpErlangTuple(elements)
}
