package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.Macro
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode.ifTag

object If {
    fun ifToMacroStringDeclaredScope(term: OtpErlangObject, scope: Scope): MacroStringDeclaredScope? =
            ifTag(term, TAG) { toMacroStringDeclaredScope(it, scope) }

    fun toMacroStringDeclaredScope(term: OtpErlangTuple, scope: Scope): MacroStringDeclaredScope =
            toMacroString(term, scope).let { MacroStringDeclaredScope(it, Scope.EMPTY) }

    private const val TAG = "if"

    private fun clausesString(term: OtpErlangTuple, scope: Scope): String =
            toClauses(term)
                    ?.let { clausesToString(it, scope) }
                    ?: "missing_clauses"

    private fun clausesToString(caseClauses: OtpErlangList, scope: Scope): String =
            caseClauses
                    .joinToString("\n") {
                        Clause.ifTo(it) {
                            clauseToString(it, scope)
                        } ?:
                        "unknown_clause"
                    }
                    .let { Macro.adjustNewLines(it, "\n  ") }

    private fun clausesToString(caseClauses: OtpErlangObject, scope: Scope): String =
            when (caseClauses) {
                is OtpErlangList -> clausesToString(caseClauses, scope)
                else -> "unknown_clauses"
            }

    // no patterns since Erlang if can only use guards
    private fun clauseToString(clause: OtpErlangTuple, scope: Scope): String {
        // cannot use Clause.guardSequenceMacroString because it adds `when ` prefix
        val guardsString = Clause.guardsString(clause)
        val bodyString = Clause.bodyString(clause, scope)

        return "$guardsString ->\n" +
                "  $bodyString"
    }

    private fun toClauses(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(2)

    private fun toMacroString(term: OtpErlangTuple, scope: Scope): MacroString {
        val clausesString = clausesString(term, scope)
        val string = "cond do\n" +
                "  $clausesString\n" +
                "end"

        return MacroString(string, doBlock = true)
    }
}
