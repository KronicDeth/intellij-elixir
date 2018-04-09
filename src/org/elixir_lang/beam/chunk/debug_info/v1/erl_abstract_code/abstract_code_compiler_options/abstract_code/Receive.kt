package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.Macro
import org.elixir_lang.Macro.adjustNewLines
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode.ifTag
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.clause.Body

object Receive {
    fun ifToMacroStringDeclaredScope(term: OtpErlangObject, scope: Scope): MacroStringDeclaredScope? =
            ifTag(term, TAG) { toMacroStringDeclaredScope(it, scope) }

    fun toMacroStringDeclaredScope(term: OtpErlangTuple, scope: Scope) =
            toMacroString(term, scope).let { MacroStringDeclaredScope(it, scope) }

    private const val TAG = "receive"

    private fun afterMacroString(term: OtpErlangTuple, scope: Scope): String {
        val arity = term.arity()

        return when (arity) {
            3 -> ""
            5 -> afterToMacroString(term.elementAt(3), term.elementAt(4), scope)
            else -> "unknown_receive_arity($arity)"
        }
    }

    private fun afterToMacroString(expression: OtpErlangObject, body: OtpErlangObject, scope: Scope): String {
        val (expressionMacroString, expressionDeclaredScope) = AbstractCode.toMacroStringDeclaredScope(expression, scope)
        val bodyMacroString = Body
                .toMacroStringDeclaredScope(body, scope.union(expressionDeclaredScope))
                .macroString
                .let { adjustNewLines(it, "\n    ") }

        return "after\n" +
                "  $expressionMacroString ->\n" +
                "    $bodyMacroString"
    }

    private fun clausesMacroString(term: OtpErlangTuple, scope: Scope): String =
            toClauses(term)
                    ?.let { clausesToMacroString(it, scope) }
                    ?: "missing_clauses"


    private fun clausesToMacroString(caseClauses: OtpErlangList, scope: Scope): String =
            caseClauses
                    .joinToString("\n") {
                        Clause.ifToMacroString(it, scope) ?:
                        "unknown_clause"
                    }
                    .let { Macro.adjustNewLines(it, "\n  ") }

    private fun clausesToMacroString(caseClauses: OtpErlangObject, scope: Scope): String =
            when (caseClauses) {
                is OtpErlangList -> clausesToMacroString(caseClauses, scope)
                else -> "unknown_clauses"
            }

    private fun toClauses(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(2)

    private fun toMacroString(term: OtpErlangTuple, scope: Scope): String {
        val clausesMacroString = clausesMacroString(term, scope)
        val afterMacroString = afterMacroString(term, scope)

        return "receive do\n" +
                "  $clausesMacroString\n" +
                "$afterMacroString\n" +
                "end"
    }
}
