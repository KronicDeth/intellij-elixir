package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangAtom
import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.Macro
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode.ifTag

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

        // Erlang's `fun Name(...) -> ... end` binds `Name` inside the body so it can recurse.  Elixir
        // has no named anonymous function - an intentional design choice (recursion goes through
        // module `def`s; see the `.()` call syntax rationale in Elixir's anonymous-functions guide).
        // We bind the fn to `Name` in the enclosing scope (`(name = fn ... end)`) so a self-reference
        // in the body has a visible antecedent instead of appearing from nowhere.  This still will NOT
        // compile when the body calls itself, because Elixir cannot see `name` inside its own
        // definition - the comment records that caveat.  The wrapping parens keep the whole thing a
        // single self-delimiting expression, safe as a match RHS, a call target, or an argument.
        val string = "($nameString = fn\n" +
                "  # Decompiled from an Erlang named fun. Elixir has no named anonymous functions, so\n" +
                "  # `$nameString` is bound to the fn here to keep self-references readable; it will still\n" +
                "  # NOT compile if the body recurses, as Elixir cannot see `$nameString` inside its own\n" +
                "  # definition (recursion must go through a module function).\n" +
                "  $clausesString\n" +
                "end)"

        return MacroString(string, doBlock = false)
    }

    private fun nameString(term: OtpErlangTuple): String =
            toName(term)
                    ?.let { nameToString(it) }
                    ?: AbstractCode.missing("named_fun_name", "${TAG} name", term)

    private fun toName(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(2)

    private fun nameToString(name: OtpErlangObject) =
            when (name) {
                is OtpErlangAtom -> Var.nameToString(name)
                else -> AbstractCode.unknown("${TAG}_name", "${TAG} name", name)
            }

    private fun clausesString(term: OtpErlangTuple, scope: Scope): String =
            toClauses(term)
                    ?.let { clausesToString(it, scope) }
                    ?: AbstractCode.missing("clauses", "${TAG} clauses", term)

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
                        AbstractCode.unknown("clause", "${TAG} clause", clauses)
                    }
                    .let { Macro.adjustNewLines(it, "\n  ") }
}
