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
        val macroString = toMacroString(term, scope)

        return MacroStringDeclaredScope(macroString, scope)
    }

    private const val TAG = "case"

    private fun clausesMacroString(term: OtpErlangTuple, scope: Scope): String =
            toClauses(term)
                    ?.let { clausesToMacroString(it, scope) }
                    ?: "missing_clauses"

    private fun clausesToMacroString(clauses: OtpErlangList, scope: Scope): String =
            clauses
                    .joinToString("\n") {
                        Clause.ifToMacroString(it, scope) ?:
                        "unknown_clause"
                    }
                    .let { adjustNewLines(it, "\n  ") }

    private fun clausesToMacroString(clauses: OtpErlangObject, scope: Scope): String =
            when (clauses) {
                is OtpErlangList -> clausesToMacroString(clauses, scope)
                else -> "unknown_clauses"
            }

    private fun expressionMacroStringDeclaredScope(term: OtpErlangTuple, scope: Scope): MacroStringDeclaredScope =
            toExpression(term)
                    ?.let { AbstractCode.toMacroStringDeclaredScope(it, scope) }
                    ?: MacroStringDeclaredScope("missing_expression", scope)

    private fun toClauses(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(3)
    private fun toExpression(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(2)

    private fun toMacroString(term: OtpErlangTuple, scope: Scope): String {
        val (expressionMacroString, expressionDeclaredScope) = expressionMacroStringDeclaredScope(term, scope)
        val clausesMacroString = clausesMacroString(term, expressionDeclaredScope)

        return "case $expressionMacroString do\n" +
                "  $clausesMacroString\n" +
                "end"
    }
}
