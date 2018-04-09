package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode.ifTag

object MapField {
    fun ifToMacroStringDeclaredScope(term: OtpErlangObject, scope: Scope): MacroStringDeclaredScope? =
            ifTag(term, TAG_SET) { toMacroStringDeclaredScope(it, scope) }

    private val TAG_SET = setOf("map_field_assoc", "map_field_exact")

    private fun keyMacroStringDeclaredScope(mapFieldAssociation: OtpErlangTuple, scope: Scope) =
            toKey(mapFieldAssociation)
                    ?.let { AbstractCode.toMacroStringDeclaredScope(it, scope) }
                    ?: MacroStringDeclaredScope("missing_key", Scope.EMPTY)

    private fun toKey(mapFieldAssociation: OtpErlangTuple): OtpErlangObject? = mapFieldAssociation.elementAt(2)

    private fun toMacroStringDeclaredScope(
            mapFieldAssociation: OtpErlangTuple,
            scope: Scope
    ): MacroStringDeclaredScope {
        val (keyMacroString, keyDeclaredScope) = keyMacroStringDeclaredScope(mapFieldAssociation, scope)
        val (valueMacroString, valueDeclaredScope) = valueMacroStringDeclaredScope(mapFieldAssociation, scope)

        return MacroStringDeclaredScope(
                "$keyMacroString => $valueMacroString",
                keyDeclaredScope.union(valueDeclaredScope)
        )
    }

    private fun toValue(mapFieldAssociation: OtpErlangTuple): OtpErlangObject? = mapFieldAssociation.elementAt(3)

    private fun valueMacroStringDeclaredScope(mapFieldAssociation: OtpErlangTuple, scope: Scope) =
            toValue(mapFieldAssociation)
                    ?.let { AbstractCode.toMacroStringDeclaredScope(it, scope) }
                    ?: MacroStringDeclaredScope("missing_value", Scope.EMPTY)

}
