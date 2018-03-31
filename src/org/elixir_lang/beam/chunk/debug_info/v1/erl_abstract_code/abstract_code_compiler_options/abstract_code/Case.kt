package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.Macro.adjustNewLines
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode

object Case {
    fun ifToMacroString(term: OtpErlangObject?): String? = AbstractCode.ifTag(term, TAG) { toMacroString(it) }

    fun toMacroString(term: OtpErlangTuple): String {
        val expressionMacroString = expressionMacroString(term)
        val clausesMacroString = clausesMacroString(term)

        return "case $expressionMacroString do\n" +
                "  $clausesMacroString\n" +
                "end"
    }

    private const val TAG = "case"

    private fun clausesMacroString(term: OtpErlangTuple): String =
            toClauses(term)
                    ?.let { clausesToMacroString(it) }
                    ?: "missing_clauses"

    private fun clausesToMacroString(clauses: OtpErlangList): String =
            clauses
                    .joinToString("\n") {
                        Clause.ifToMacroString(it) ?:
                        "unknown_clause"
                    }
                    .let { adjustNewLines(it, "\n  ") }

    private fun clausesToMacroString(clauses: OtpErlangObject): String =
            when (clauses) {
                is OtpErlangList -> clausesToMacroString(clauses)
                else -> "unknown_clauses"
            }

    private fun expressionMacroString(term: OtpErlangTuple): String =
            toExpression(term)
                    ?.let { AbstractCode.toMacroString(it) }
                    ?: "missing_expression"

    private fun toClauses(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(3)
    private fun toExpression(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(2)
}
