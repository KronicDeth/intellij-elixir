package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.Macro.adjustNewLines
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode.ifTag
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.clause.Body

object Try {
    fun ifToMacroStringDeclaredScope(term: OtpErlangObject, scope: Scope): MacroStringDeclaredScope? =
            ifTag(term, TAG) { toMacroStringDeclaredScope(it, scope) }

    fun toMacroStringDeclaredScope(term: OtpErlangTuple, scope: Scope) =
            toMacroString(term, scope).let { MacroStringDeclaredScope(it, Scope.EMPTY) }

    private const val TAG = "try"

    private fun bodyMacroString(term: OtpErlangTuple, scope: Scope) =
            toBody(term)
                    ?.let { Body.toMacroStringDeclaredScope(it, scope).macroString }
                    ?: "missing_body"

    private fun caseClausesMacroString(term: OtpErlangTuple, scope: Scope) =
            toCaseClauses(term)
                    ?.let { caseClausesToMacroString(it, scope) }
                    ?: "missing_case_clauses"

    private fun caseClausesToMacroString(caseClauses: OtpErlangList, scope: Scope) =
            caseClauses
                    .joinToString("\n") {
                        Clause.ifToMacroString(it, scope) ?:
                        "unknown_case_clause"
                    }
                    .let { adjustNewLines(it, "\n  ") }

    private fun caseClausesToMacroString(caseClauses: OtpErlangObject, scope: Scope) =
            when (caseClauses) {
                is OtpErlangList -> caseClausesToMacroString(caseClauses, scope)
                else -> "unknown_case_clauses"
            }

    private fun catchClausesMacroString(term: OtpErlangTuple, scope: Scope) =
            toCatchClauses(term)
                    ?.let { catchClausesToMacroString(it, scope) }
                    ?: "missing_catch_clauses"

    private fun catchClausesToMacroString(catchClauses: OtpErlangList, scope: Scope) =
            catchClauses
                    .joinToString("\n") {
                        Clause.ifToMacroString(it, scope) ?:
                        "unknown_catch_clause"
                    }
                    .let { adjustNewLines(it, "\n  ") }

    private fun catchClausesToMacroString(catchClauses: OtpErlangObject, scope: Scope) =
            when (catchClauses) {
                is OtpErlangList -> catchClausesToMacroString(catchClauses, scope)
                else -> "unknown_catch_clauses"
            }

    private fun toBody(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(2)
    private fun toCaseClauses(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(3)
    private fun toCatchClauses(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(4)

    private fun toMacroString(term: OtpErlangTuple, scope: Scope): String {
        val bodyMacroString = bodyMacroString(term, scope)
        val catchClausesMacroString = catchClausesMacroString(term, scope)
        val caseClausesMacroString = caseClausesMacroString(term, scope)

        return "try do\n" +
                "  $bodyMacroString\n" +
                "catch\n" +
                "  $catchClausesMacroString\n" +
                "else\n" +
                "  $caseClausesMacroString\n" +
                "end"
    }
}
