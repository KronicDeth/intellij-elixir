package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.Macro.adjustNewLines
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode.ifTag

object Case {
    fun ifToMacroStringDeclaredScope(term: OtpErlangObject?, scope: Scope): MacroStringDeclaredScope? =
            ifTag(term, TAG) { toMacroStringDeclaredScope(it, scope) }

    fun toMacroStringDeclaredScope(term: OtpErlangTuple, scope: Scope): MacroStringDeclaredScope {
        val string = toString(term, scope)

        return MacroStringDeclaredScope(string, doBlock = true, Scope.EMPTY)
    }

    private const val TAG = "case"

    private fun clausesString(term: OtpErlangTuple, scope: Scope): String =
            toClauses(term)
                    ?.let { clausesToString(it, scope) }
                    ?: "missing_clauses"

    private fun clausesToString(clauses: OtpErlangList, scope: Scope): String =
            clauses
                    .joinToString("\n") {
                        Clause.ifToString(it, scope) ?:
                        "unknown_clause"
                    }
                    .let { adjustNewLines(it, "\n  ") }

    private fun clausesToString(clauses: OtpErlangObject, scope: Scope): String =
            when (clauses) {
                is OtpErlangList -> clausesToString(clauses, scope)
                else -> "unknown_clauses"
            }

    private fun expressionMacroStringDeclaredScope(term: OtpErlangTuple, scope: Scope): MacroStringDeclaredScope =
            toExpression(term)
                    ?.let { AbstractCode.toMacroStringDeclaredScope(it, scope) }
                    ?: MacroStringDeclaredScope("missing_expression", doBlock = false, scope)

    private fun toClauses(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(3)
    private fun toExpression(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(2)

    private fun toString(term: OtpErlangTuple, scope: Scope): String {
        val (expressionMacroString, expressionDeclaredScope) = expressionMacroStringDeclaredScope(term, scope)
        val clausesString = clausesString(term, expressionDeclaredScope)
        val macroStringBuilder = StringBuilder()

        if (expressionMacroString.doBlock) {
            macroStringBuilder
                    .append(expressionMacroString.string).append('\n')
                    .append("|> case do\n")
        } else {
            macroStringBuilder.append("case ${expressionMacroString.string} do\n")
        }

        macroStringBuilder
                .append("  $clausesString\n")
                .append("end")

        return macroStringBuilder.toString()
    }
}
