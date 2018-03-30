package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.comprehension.Qualifiers

object BitstringComprehension {
    fun ifToMacroString(term: OtpErlangObject?): String? = AbstractCode.ifTag(term, TAG) { toMacroString(it) }

    fun toMacroString(term: OtpErlangTuple): String {
        val expressionMacroString = expressionMacroString(term)
        val qualifiersMacroString = qualifiersMacroString(term)

        return "for $qualifiersMacroString do\n" +
                "  $expressionMacroString\n" +
                "end"
    }

    private const val TAG = "bc"

    private fun expressionMacroString(term: OtpErlangTuple): String =
            toExpression(term)
                    ?.let { AbstractCode.toMacroString(it) }
                    ?: "missing_expression"

    private fun qualifiersMacroString(term: OtpErlangTuple): String =
            toQualifiers(term)
                    ?.let { Qualifiers.toMacroString(it) }
                    ?: "missing_qualifiers"

    private fun toExpression(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(2)
    private fun toQualifiers(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(3)
}
