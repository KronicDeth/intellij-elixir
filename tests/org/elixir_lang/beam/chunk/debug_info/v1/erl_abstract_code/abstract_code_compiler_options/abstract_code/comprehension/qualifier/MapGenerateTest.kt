package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.comprehension.qualifier

import com.ericsson.otp.erlang.OtpErlangAtom
import com.ericsson.otp.erlang.OtpErlangLong
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import junit.framework.TestCase
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Scope

class MapGenerateTest : TestCase() {

    /**
     * Verifies that [MapGenerate] is recognised for an `m_generate` term (OTP 26 map generator)
     * and not routed to the unknown-handler.
     *
     * Erlang source equivalent: `TimeoutType := {_TimerRef, TimeoutMsg} <- Timers`
     * (from `gen_statem` internals)
     *
     * AST: `{m_generate, {5412,49},
     *         {map_field_exact, {5412,22}, {var,{5412,10},'TimeoutType'}, {var,{5412,25},'Value'}},
     *         {var, {5412,52}, 'Timers'}}`
     */
    fun testIfToMacroStringDeclaredScopeReturnsNonNullForMGenerate() {
        val line = OtpErlangLong(1)

        val key = tuple(OtpErlangAtom("var"), line, OtpErlangAtom("TimeoutType"))
        val value = tuple(OtpErlangAtom("var"), line, OtpErlangAtom("Value"))
        val pattern = tuple(OtpErlangAtom("map_field_exact"), line, key, value)
        val expression = tuple(OtpErlangAtom("var"), line, OtpErlangAtom("Timers"))
        val mGenerate = tuple(OtpErlangAtom("m_generate"), line, pattern, expression)

        val result = MapGenerate.ifToMacroStringDeclaredScope(mGenerate, Scope.EMPTY)

        assertNotNull("m_generate should be handled, not fall through to unknown", result)
    }

    /**
     * Verifies that the rendered string contains the `<-` generator arrow and
     * references to both the pattern and the expression variable.
     *
     * A variable-key `map_field_exact` pattern like `K := V` renders as something
     * containing `<-` (generator arrow) and the expression variable name lowercased.
     */
    fun testToMacroStringDeclaredScopeContainsArrowAndExpression() {
        val line = OtpErlangLong(1)

        val key = tuple(OtpErlangAtom("var"), line, OtpErlangAtom("K"))
        val value = tuple(OtpErlangAtom("var"), line, OtpErlangAtom("V"))
        val pattern = tuple(OtpErlangAtom("map_field_exact"), line, key, value)
        val expression = tuple(OtpErlangAtom("var"), line, OtpErlangAtom("MyMap"))
        val mGenerate = tuple(OtpErlangAtom("m_generate"), line, pattern, expression)

        val result = MapGenerate.toMacroStringDeclaredScope(mGenerate, Scope.EMPTY)
        val string = result.macroString.string

        assertTrue("Result should contain <- generator arrow, got: $string", string.contains("<-"))
        assertTrue("Result should reference expression variable, got: $string", string.contains("myMap"))
    }

    /**
     * Verifies that a non-`m_generate` tag returns null from [MapGenerate.ifToMacroStringDeclaredScope],
     * so it does not intercept terms meant for other handlers.
     */
    fun testIfToMacroStringDeclaredScopeReturnsNullForOtherTags() {
        val line = OtpErlangLong(1)
        val generate = tuple(OtpErlangAtom("generate"), line,
            tuple(OtpErlangAtom("var"), line, OtpErlangAtom("X")),
            tuple(OtpErlangAtom("var"), line, OtpErlangAtom("List")))

        val result = MapGenerate.ifToMacroStringDeclaredScope(generate, Scope.EMPTY)

        assertNull("Non-m_generate tag should return null", result)
    }

    private fun tuple(vararg elements: OtpErlangObject): OtpErlangTuple = OtpErlangTuple(elements)
}
