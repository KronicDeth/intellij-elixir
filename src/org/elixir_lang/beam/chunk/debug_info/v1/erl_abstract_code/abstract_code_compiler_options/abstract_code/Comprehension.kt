package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode.ifTag
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.comprehension.Qualifiers

object Comprehension {
    fun ifToMacroStringDeclaredScope(term: OtpErlangObject, scope: Scope): MacroStringDeclaredScope? =
            ifTag(term, "bc") { toMacroStringDeclaredScope(it, scope) } ?:
            ifTag(term, "lc") { toMacroStringDeclaredScope(it, scope) }

    fun toMacroStringDeclaredScope(term: OtpErlangTuple, scope: Scope): MacroStringDeclaredScope {
        val (qualifiersMacroString, qualifiersDeclaredScope) = qualifiersMacroStringDeclaredScope(term, scope)
        val groupedQualifierString = qualifiersMacroString.group().string
        val expressionString = expressionString(term, qualifiersDeclaredScope)

        val string = "for $groupedQualifierString do\n" +
                "  $expressionString\n" +
                "end"

        return MacroStringDeclaredScope(string, doBlock = true, Scope.EMPTY)
    }

    private fun expressionString(term: OtpErlangTuple, scope: Scope): String =
            toExpression(term)
                    ?.let { AbstractCode.toString(it, scope) }
                    ?:"missing_expression"

    private fun qualifiersMacroStringDeclaredScope(term: OtpErlangTuple, scope: Scope): MacroStringDeclaredScope =
            toQualifiers(term)
                    ?.let { Qualifiers.toMacroStringDeclaredScope(it, scope) }
                    ?: MacroStringDeclaredScope("missing_qualifiers", doBlock = false, scope)

    private fun toExpression(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(2)
    private fun toQualifiers(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(3)
}
