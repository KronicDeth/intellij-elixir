package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.Macro.adjustNewLines
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.clause.Body

object Try {
    fun ifToMacroString(term: OtpErlangObject): String? = AbstractCode.ifTag(term, TAG) { toMacroString(it) }

    fun toMacroString(term: OtpErlangTuple): String {
        val bodyMacroString = bodyMacroString(term)
        val catchClausesMacroString = catchClausesMacroString(term)
        val caseClausesMacroString = caseClausesMacroString(term)

        return "try do\n" +
                "  $bodyMacroString\n" +
                "catch\n" +
                "  $catchClausesMacroString\n" +
                "else\n" +
                "  $caseClausesMacroString\n" +
                "end"
    }

    private const val TAG = "try"

    private fun bodyMacroString(term: OtpErlangTuple): String =
            toBody(term)
                    ?.let { Body.toMacroString(it) }
                    ?: "missing_body"

    private fun caseClausesMacroString(term: OtpErlangTuple): String =
            toCaseClauses(term)
                    ?.let { caseClausesToMacroString(it) }
                    ?: "missing_case_clauses"

    private fun caseClausesToMacroString(caseClauses: OtpErlangList): String =
            caseClauses
                    .joinToString("\n") {
                        Clause.ifToMacroString(it) ?:
                        "unknown_case_clause"
                    }
                    .let { adjustNewLines(it, "\n  ") }

    private fun caseClausesToMacroString(caseClauses: OtpErlangObject): String =
            when (caseClauses) {
                is OtpErlangList -> caseClausesToMacroString(caseClauses)
                else -> "unknown_case_clauses"
            }

    private fun catchClausesMacroString(term: OtpErlangTuple): String =
            toCatchClauses(term)
                    ?.let { catchClausesToMacroString(it) }
                    ?: "missing_catch_clauses"

    private fun catchClausesToMacroString(catchClauses: OtpErlangList): String =
            catchClauses
                    .joinToString("\n") {
                        Clause.ifToMacroString(it) ?:
                        "unknown_catch_clause"
                    }
                    .let { adjustNewLines(it, "\n  ") }

    private fun catchClausesToMacroString(catchClauses: OtpErlangObject): String =
            when (catchClauses) {
                is OtpErlangList -> catchClausesToMacroString(catchClauses)
                else -> "unknown_catch_clauses"
            }

    private fun toBody(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(2)
    private fun toCaseClauses(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(3)
    private fun toCatchClauses(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(4)
}
