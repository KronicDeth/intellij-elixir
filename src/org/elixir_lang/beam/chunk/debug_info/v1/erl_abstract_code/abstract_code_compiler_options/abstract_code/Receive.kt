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
    fun ifToMacroString(term: OtpErlangObject): String? = ifTag(term, TAG) { toMacroString(it) }

    fun toMacroString(term: OtpErlangTuple): String {
        val clausesMacroString = clausesMacroString(term)
        val afterMacroString = afterMacroString(term)

        return "receive do\n" +
                "  $clausesMacroString\n" +
                "$afterMacroString\n" +
                "end"
    }

    private const val TAG = "receive"

    private fun afterMacroString(term: OtpErlangTuple): String {
        val arity = term.arity()

        return when (arity) {
            3 -> ""
            5 -> afterToMacroString(term.elementAt(3), term.elementAt(4))
            else -> "unknown_receive_arity($arity)"
        }
    }

    private fun afterToMacroString(expression: OtpErlangObject, body: OtpErlangObject): String {
        val expressionMacroString = AbstractCode.toMacroString(expression)
        val bodyMacroString = Body
                .toMacroString(body)
                .let { adjustNewLines(it, "\n    ") }

        return "after\n" +
                "  $expressionMacroString ->\n" +
                "    $bodyMacroString"
    }

    private fun clausesMacroString(term: OtpErlangTuple): String =
            toClauses(term)
                    ?.let { clausesToMacroString(it) }
                    ?: "missing_clauses"


    private fun clausesToMacroString(caseClauses: OtpErlangList): String =
            caseClauses
                    .joinToString("\n") {
                        Clause.ifToMacroString(it) ?:
                        "unknown_clause"
                    }
                    .let { Macro.adjustNewLines(it, "\n  ") }

    private fun clausesToMacroString(caseClauses: OtpErlangObject): String =
            when (caseClauses) {
                is OtpErlangList -> clausesToMacroString(caseClauses)
                else -> "unknown_clauses"
            }

    private fun toClauses(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(2)
}
