package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.Macro
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode.ifTag

object If {
    fun ifToMacroString(term: OtpErlangObject): String? = ifTag(term, TAG) { toMacroString(it) }

    fun toMacroString(term: OtpErlangTuple): String {
        val clausesMacroString = clausesMacroString(term)

        return "cond do\n" +
                "  $clausesMacroString\n" +
                "end"
    }

    private const val TAG = "if"

    private fun clausesMacroString(term: OtpErlangTuple): String =
            toClauses(term)
                    ?.let { clausesToMacroString(it) }
                    ?: "missing_clauses"

    private fun clausesToMacroString(caseClauses: OtpErlangList): String =
            caseClauses
                    .joinToString("\n") {
                        Clause.ifTo(it) {
                            clauseToMacroString(it)
                        } ?:
                        "unknown_clause"
                    }
                    .let { Macro.adjustNewLines(it, "\n  ") }

    private fun clausesToMacroString(caseClauses: OtpErlangObject): String =
            when (caseClauses) {
                is OtpErlangList -> clausesToMacroString(caseClauses)
                else -> "unknown_clauses"
            }

    // no patterns since Erlang if can only use guards
    private fun clauseToMacroString(clause: OtpErlangTuple): String {
        // cannot use Clause.guardSequenceMacroString because it adds `when ` prefix
        val guardsMacroString = Clause.guardsMacroString(clause)
        val bodyMacroString = Clause.bodyMacroString(clause)

        return "$guardsMacroString ->\n" +
                "  $bodyMacroString"
    }

    private fun toClauses(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(2)
}
