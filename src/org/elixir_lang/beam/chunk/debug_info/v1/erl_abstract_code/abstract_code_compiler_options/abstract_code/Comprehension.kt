package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode.ifTag
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.comprehension.Qualifiers

object Comprehension {
    fun ifToMacroStringDeclaredScope(term: OtpErlangObject, scope: Scope): MacroStringDeclaredScope? =
            ifTag(term, "bc") { toMacroStringDeclaredScope(it, scope) } ?:
            ifTag(term, "lc") { toMacroStringDeclaredScope(it, scope) } ?:
            ifTag(term, "mc") { toMapMacroStringDeclaredScope(it, scope) }

    fun toMacroStringDeclaredScope(term: OtpErlangTuple, scope: Scope): MacroStringDeclaredScope {
        val (qualifiersMacroString, qualifiersDeclaredScope) = qualifiersMacroStringDeclaredScope(term, scope)
        val groupedQualifierString = qualifiersMacroString.group().string
        val expressionString = expressionString(term, qualifiersDeclaredScope)

        val string = "for $groupedQualifierString do\n" +
                "  $expressionString\n" +
                "end"

        return MacroStringDeclaredScope(string, doBlock = true, Scope.EMPTY)
    }

    /**
     * A map comprehension (`mc`, `#{K => V || ...}`) builds a map, so it needs `into: %{}` and its
     * body must be a `{key, value}` tuple: the Erlang abstract form's element expression is a
     * `map_field_assoc` (`K => V`), but a bare `key => value` is only valid Elixir inside a `%{}`
     * literal, not as a comprehension body.
     */
    private fun toMapMacroStringDeclaredScope(term: OtpErlangTuple, scope: Scope): MacroStringDeclaredScope {
        val (qualifiersMacroString, qualifiersDeclaredScope) = qualifiersMacroStringDeclaredScope(term, scope)
        val groupedQualifierString = qualifiersMacroString.group().string
        val entryString = mapEntryString(term, qualifiersDeclaredScope)

        val string = "for $groupedQualifierString, into: %{} do\n" +
                "  $entryString\n" +
                "end"

        return MacroStringDeclaredScope(string, doBlock = true, Scope.EMPTY)
    }

    private fun mapEntryString(term: OtpErlangTuple, scope: Scope): String =
            ifTag(toExpression(term), "map_field_assoc") { association ->
                val key = association.elementAt(2)
                val value = association.elementAt(3)

                if (key != null && value != null) {
                    "{${AbstractCode.toString(key, scope)}, ${AbstractCode.toString(value, scope)}}"
                } else {
                    AbstractCode.missing("map_entry", "map comprehension entry", association)
                }
            } ?: expressionString(term, scope)

    private fun expressionString(term: OtpErlangTuple, scope: Scope): String =
            toExpression(term)
                    ?.let { AbstractCode.toString(it, scope) }
                    ?: AbstractCode.missing("expression", "comprehension expression", term)

    private fun qualifiersMacroStringDeclaredScope(term: OtpErlangTuple, scope: Scope): MacroStringDeclaredScope =
            toQualifiers(term)
                    ?.let { Qualifiers.toMacroStringDeclaredScope(it, scope) }
                    ?: MacroStringDeclaredScope.missing("qualifiers", scope, "comprehension qualifiers", term)

    private fun toExpression(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(2)
    private fun toQualifiers(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(3)
}
