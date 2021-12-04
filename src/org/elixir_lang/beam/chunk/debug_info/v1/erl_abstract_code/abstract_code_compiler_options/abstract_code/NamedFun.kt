package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangAtom
import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.Macro
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode.ifTag
import org.elixir_lang.code.Identifier.inspectAsFunction

object NamedFun {
    fun <T> ifTo(term: OtpErlangObject?, ifTrue: (OtpErlangTuple) -> T?): T? = ifTag(term, TAG, ifTrue)

    fun ifToMacroStringDeclaredScope(term: OtpErlangObject, scope: Scope): MacroStringDeclaredScope? =
            ifTo(term) { toMacroStringDeclaredScope(it, scope) }

    fun toMacroStringDeclaredScope(term: OtpErlangTuple, scope: Scope): MacroStringDeclaredScope =
        toMacroString(term, scope)
                .let { MacroStringDeclaredScope(it, Scope.EMPTY) }

    private const val TAG = "named_fun"

    private fun toMacroString(term: OtpErlangTuple, scope: Scope): MacroString {
        val nameString = nameString(term)
        val clausesString = clausesString(term, scope)

        // this is a fake macro.  Elixir does not support named anonymous function like Erlang does
        val string = "named_anonymous_function $nameString do\n" +
                "  $clausesString\n" +
                "end"

        return MacroString(string, doBlock = true)
    }

    private fun nameString(term: OtpErlangTuple): String =
            toName(term)
                    ?.let { nameToString(it) }
                    ?: "missing_named_fun_name"

    private fun toName(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(2)

    private fun nameToString(name: OtpErlangObject) =
            when (name) {
                is OtpErlangAtom -> Var.nameToString(name)
                else -> "unknown_named_fun_name"
            }

    private fun clausesString(term: OtpErlangTuple, scope: Scope): String =
            toClauses(term)
                    ?.let { clausesToString(it, scope) }
                    ?: "missing_clauses"

    private fun toClauses(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(3)

    private fun clausesToString(clauses: OtpErlangObject?, scope: Scope): String =
            when (clauses) {
                is OtpErlangList -> clausesToString(clauses, scope)
                else -> "unknown_clauses"
            }

    private fun clausesToString(clauses: OtpErlangList, scope: Scope): String =
            clauses
                    .joinToString("\n") {
                        Clause.ifToString(it, scope) ?:
                        "unknown_clause"
                    }
                    .let { Macro.adjustNewLines(it, "\n  ") }
}
