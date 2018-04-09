package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.`fun`

import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.Macro
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode.ifTag
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Clause
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.MacroString
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Scope

object Clauses {
    fun ifToMacroString(term: OtpErlangObject, scope: Scope): MacroString? =
            ifTag(term, TAG) { toMacroString(it, scope) }

    fun toMacroString(term: OtpErlangTuple, scope: Scope): MacroString {
        val clausesMacroString = clausesMacroString(term, scope)

        return "fn $clausesMacroString\n" +
                "end"
    }

    private const val TAG = "clauses"

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
                    .let { Macro.adjustNewLines(it, "\n  ") }

    private fun clausesToMacroString(clauses: OtpErlangObject, scope: Scope): String =
            when (clauses) {
                is OtpErlangList -> clausesToMacroString(clauses, scope)
                else -> "unknown_clauses"
            }

    private fun toClauses(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(1)
}
