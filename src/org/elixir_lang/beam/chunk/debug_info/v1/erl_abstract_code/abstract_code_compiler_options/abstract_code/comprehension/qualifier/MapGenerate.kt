package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.comprehension.qualifier

import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode.ifTag
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.MacroStringDeclaredScope
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Scope

/**
 * Handles the `m_generate` abstract code node introduced in OTP 26 for map comprehension
 * generators: `Key := Value <- MapExpression`.
 *
 * Structure: `{m_generate, Anno, Pattern, Expression}`
 * - Pattern  (element 2): a `map_field_exact`/`map_field_assoc` tuple
 *   (`{map_field_exact, Anno, KeyPattern, ValuePattern}`)
 * - Expression (element 3): the source map expression
 *
 * Iterating a map in an Elixir comprehension yields `{key, value}` tuples, so the key/value patterns are
 * rendered as a two-element tuple pattern `{key_pattern, value_pattern} <- expression`, consistent with list
 * and bitstring generators.
 */
object MapGenerate {
    fun ifToMacroStringDeclaredScope(term: OtpErlangObject, scope: Scope): MacroStringDeclaredScope? =
            ifTag(term, TAG) { toMacroStringDeclaredScope(it, scope) }

    fun toMacroStringDeclaredScope(term: OtpErlangTuple, scope: Scope): MacroStringDeclaredScope {
        val (expressionMacroString, expressionDeclaredScope) = expressionMacroStringDeclaredScope(term, scope)
        val patternScope = scope.union(expressionDeclaredScope)
        val (patternMacroString, patternDeclaredScope) = patternMacroStringDeclaredScope(term, patternScope)
        val macroString = "${patternMacroString.string} <- ${expressionMacroString.group().string}"
        val declaredScope = patternScope.union(patternDeclaredScope)

        return MacroStringDeclaredScope(macroString, doBlock = false, declaredScope)
    }

    private const val TAG = "m_generate"

    private fun expressionMacroStringDeclaredScope(term: OtpErlangTuple, scope: Scope) =
            toExpression(term)
                    ?.let { AbstractCode.toMacroStringDeclaredScope(it, scope) }
                    ?: MacroStringDeclaredScope.missing("expression", scope, "map generate expression", term)

    private fun patternMacroStringDeclaredScope(term: OtpErlangTuple, scope: Scope): MacroStringDeclaredScope {
        val pattern = toPattern(term)

        return if (pattern is OtpErlangTuple) {
            val (keyMacroString, keyDeclaredScope) = keyMacroStringDeclaredScope(pattern, scope)
            val valueScope = scope.union(keyDeclaredScope)
            val (valueMacroString, valueDeclaredScope) = valueMacroStringDeclaredScope(pattern, valueScope)

            MacroStringDeclaredScope(
                    "{${keyMacroString.string}, ${valueMacroString.string}}",
                    doBlock = false,
                    keyDeclaredScope.union(valueDeclaredScope)
            )
        } else {
            MacroStringDeclaredScope.missing("pattern", scope, "map generate pattern", term)
        }
    }

    private fun keyMacroStringDeclaredScope(pattern: OtpErlangTuple, scope: Scope) =
            toKey(pattern)
                    ?.let { AbstractCode.toMacroStringDeclaredScope(it, scope) }
                    ?: MacroStringDeclaredScope.missing("key", scope, "map generate pattern key", pattern)

    private fun valueMacroStringDeclaredScope(pattern: OtpErlangTuple, scope: Scope) =
            toValue(pattern)
                    ?.let { AbstractCode.toMacroStringDeclaredScope(it, scope) }
                    ?: MacroStringDeclaredScope.missing("value", scope, "map generate pattern value", pattern)

    private fun toExpression(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(3)
    private fun toPattern(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(2)
    private fun toKey(pattern: OtpErlangTuple): OtpErlangObject? = pattern.elementAt(2)
    private fun toValue(pattern: OtpErlangTuple): OtpErlangObject? = pattern.elementAt(3)
}
