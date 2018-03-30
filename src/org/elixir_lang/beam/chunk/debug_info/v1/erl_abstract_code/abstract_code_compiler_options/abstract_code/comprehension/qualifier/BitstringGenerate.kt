package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.comprehension.qualifier

import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode

object BitstringGenerate {
    fun ifToMacroString(term: OtpErlangObject): String? = AbstractCode.ifTag(term, TAG) { toMacroString(it) }
    fun toMacroString(term: OtpErlangTuple): String {
        val patternMacroString = patternMacroString(term)
        val expressionMacroString = expressionMacroString(term)

        return "<<$patternMacroString <- $expressionMacroString>>"
    }

    private const val TAG = "b_generate"

    private fun expressionMacroString(term: OtpErlangTuple): String =
            toExpression(term)
                    ?.let { AbstractCode.toMacroString(it) }
                    ?: "missing_expression"

    private fun patternMacroString(term: OtpErlangTuple): String =
            toPattern(term)
                    ?.let { AbstractCode.toMacroString(it) }
                    ?: "missing_pattern"

    private fun toExpression(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(3)
    private fun toPattern(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(2)
}
