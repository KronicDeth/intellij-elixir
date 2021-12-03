package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
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

    private fun afterString(term: OtpErlangTuple, scope: Scope): String = when (val arity = term.arity()) {
        3 -> ""
        5 -> afterToMacroString(term.elementAt(3), term.elementAt(4), scope)
        else -> "unknown_receive_arity($arity)"
    }

    private fun afterToMacroString(expression: OtpErlangObject, body: OtpErlangObject, scope: Scope): String {
        val (expressionMacroString, expressionDeclaredScope) = AbstractCode.toMacroStringDeclaredScope(expression, scope)
        val bodyString = Body
                .toMacroStringDeclaredScope(body, scope.union(expressionDeclaredScope))
                .macroString
                .string
                .let { adjustNewLines(it, "\n    ") }

        return "after\n" +
                "  ${expressionMacroString.string} ->\n" +
                "    $bodyString"
    }

    private fun clausesString(term: OtpErlangTuple, scope: Scope): String =
            toClauses(term)
                    ?.let { clausesToString(it, scope) }
                    ?: "missing_clauses"


    private fun clausesToString(caseClauses: OtpErlangList, scope: Scope): String =
            caseClauses
                    .joinToString("\n") {
                        Clause.ifToString(it, scope) ?:
                        "unknown_clause"
                    }
                    .let { adjustNewLines(it, "\n  ") }

    private fun clausesToString(caseClauses: OtpErlangObject, scope: Scope): String =
            when (caseClauses) {
                is OtpErlangList -> clausesToString(caseClauses, scope)
                else -> "unknown_clauses"
            }

    private fun toClauses(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(2)

    private fun toMacroString(term: OtpErlangTuple, scope: Scope): MacroString {
        val stringBuilder = StringBuilder().append("receive do\n")

        val clausesString = clausesString(term, scope)
        if (clausesString.isNotEmpty()) {
            stringBuilder.append(clausesString).append('\n')
        }

        val afterString = afterString(term, scope)
        if (afterString.isNotEmpty()) {
            stringBuilder.append(afterString).append('\n')
        }

        stringBuilder.append("end")

        return MacroString(stringBuilder.toString(), doBlock = true)
    }
}
