package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.comprehension.qualifier

import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.MacroStringDeclaredScope
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Scope

/**
 * The `m_generate` map comprehension generator introduced in OTP 26: `Key := Value <- MapExpression`.
 *
 * The pattern (element 2) is a `map_field_exact`/`map_field_assoc` tuple
 * (`{map_field_exact, Anno, KeyPattern, ValuePattern}`).  Iterating a map in an Elixir comprehension yields
 * `{key, value}` tuples, so the key/value patterns are rendered as a two-element tuple pattern
 * `{key_pattern, value_pattern} <- expression`, consistent with list and bitstring generators.
 */
object MapGenerate : Generator("m_generate", "map generate expression") {
    override fun patternMacroStringDeclaredScope(term: OtpErlangTuple, scope: Scope): MacroStringDeclaredScope {
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

    private fun toKey(pattern: OtpErlangTuple): OtpErlangObject? = pattern.elementAt(2)
    private fun toValue(pattern: OtpErlangTuple): OtpErlangObject? = pattern.elementAt(3)
}
