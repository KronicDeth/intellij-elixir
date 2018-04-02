package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode.ifTag
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.comprehension.Qualifiers

object BitstringComprehension {
    fun ifToMacroStringDeclaredScope(term: OtpErlangObject, scope: Scope): MacroStringDeclaredScope? =
            ifTag(term, TAG) { toMacroStringDeclaredScope(it, scope) }

    fun toMacroStringDeclaredScope(term: OtpErlangTuple, scope: Scope): MacroStringDeclaredScope {
        val (qualifiersMacroString, qualifiersDeclaredScope) = qualifiersMacroStringDeclaredScope(term, scope)
        val expressionMacroString = expressionMacroString(term, qualifiersDeclaredScope)

        val macroString = "for $qualifiersMacroString do\n" +
                "  $expressionMacroString\n" +
                "end"

        return MacroStringDeclaredScope(macroString, Scope.EMPTY)
    }

    private const val TAG = "bc"

    private fun expressionMacroString(term: OtpErlangTuple, scope: Scope): String =
            toExpression(term)
                    ?.let { AbstractCode.toMacroStringDeclaredScope(it, scope).macroString }
                    ?: "missing_expression"

    private fun qualifiersMacroStringDeclaredScope(term: OtpErlangTuple, scope: Scope): MacroStringDeclaredScope =
            toQualifiers(term)
                    ?.let { Qualifiers.toMacroStringDeclaredScope(it, scope) }
                    ?: MacroStringDeclaredScope("missing_qualifiers", scope)

    private fun toExpression(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(2)
    private fun toQualifiers(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(3)
}
