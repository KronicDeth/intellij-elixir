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

    private fun bodyString(term: OtpErlangTuple, scope: Scope): String =
            toBody(term)
                    ?.let { Body.toString(it, scope) }
                    ?: "missing_body"

    private fun caseClausesString(term: OtpErlangTuple, scope: Scope): String? {
        val caseClauses = toCaseClauses(term)

        return if (caseClauses != null) {
            caseClausesToMacroString(caseClauses, scope)
        } else {
            "missing_case_clauses"
        }
    }

    private fun caseClausesToMacroString(caseClauses: OtpErlangList, scope: Scope) =
            if (caseClauses.arity() > 0) {
                caseClauses
                        .joinToString("\n") {
                            Clause.ifToString(it, scope) ?: "unknown_case_clause"
                        }
                        .let { adjustNewLines(it, "\n  ") }
            } else {
                null
            }

    private fun caseClausesToMacroString(caseClauses: OtpErlangObject, scope: Scope) =
            when (caseClauses) {
                is OtpErlangList -> caseClausesToMacroString(caseClauses, scope)
                else -> "unknown_case_clauses"
            }

    private fun catchClausesString(term: OtpErlangTuple, scope: Scope): String? {
        val catchClauses = toCatchClauses(term)

        return if (catchClauses != null) {
            catchClausesToString(catchClauses, scope)
        } else {
            "missing_catch_clauses"
        }
    }

    private fun catchClausesToString(catchClauses: OtpErlangList, scope: Scope): String? =
        if (catchClauses.arity() > 0) {
            catchClauses
                    .joinToString("\n") {
                        Clause.ifToString(it, scope) ?: "unknown_catch_clause"
                    }
                    .let { adjustNewLines(it, "\n  ") }
        } else {
            null
        }

    private fun catchClausesToString(catchClauses: OtpErlangObject, scope: Scope): String? =
            when (catchClauses) {
                is OtpErlangList -> catchClausesToString(catchClauses, scope)
                else -> "unknown_catch_clauses"
            }

    private fun toBody(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(2)
    private fun toCaseClauses(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(3)
    private fun toCatchClauses(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(4)

    private fun toMacroString(term: OtpErlangTuple, scope: Scope): MacroString {
        val macroStringBuilder = StringBuilder("try do\n")

        val bodyString = bodyString(term, scope)
        macroStringBuilder.append("  ").append(bodyString).append('\n')

        catchClausesString(term, scope)?.let { catchClausesString ->
            macroStringBuilder
                    .append("catch\n")
                    .append("  ").append(catchClausesString).append('\n')
        }

        caseClausesString(term, scope)?.let { caseClausesString ->
            macroStringBuilder
                    .append("else\n")
                    .append("  ").append(caseClausesString).append('\n')
        }

        macroStringBuilder.append("end")

        return MacroString(macroStringBuilder.toString(), doBlock = true)
    }
}
