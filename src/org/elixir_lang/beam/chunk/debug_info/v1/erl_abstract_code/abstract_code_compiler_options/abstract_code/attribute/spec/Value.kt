package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.attribute.spec

import com.ericsson.otp.erlang.OtpErlangAtom
import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.MacroString
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.attribute.spec.value.Definition
import org.elixir_lang.code.Identifier.inspectAsFunction

object Value {
    fun toMacroString(value: OtpErlangObject): MacroString =
            when (value) {
                is OtpErlangTuple -> toMacroString(value)
                else -> "unknown_spec_value"
            }

    private fun definitionsToMacroString(definitions: OtpErlangList, nameMacroString: MacroString): MacroString {
        val arity = definitions.arity()

        return when (arity) {
            1 -> Definition.toMacroString(definitions.elementAt(0), nameMacroString)
            else -> "unknown_definitions_arity($arity)"
        }
    }

    private fun definitionsToMacroString(definitions: OtpErlangObject, nameMacroString: MacroString) =
            when (definitions) {
                is OtpErlangList -> definitionsToMacroString(definitions, nameMacroString)
                else -> "unknown_definitions"
            }

    private fun nameArityToName(nameArity: OtpErlangObject) =
            when (nameArity) {
                is OtpErlangTuple -> nameArityToName(nameArity)
                else -> null
            }

    private fun nameArityToName(nameArity: OtpErlangTuple): OtpErlangObject? = nameArity.elementAt(0)

    private fun nameMacroString(value: OtpErlangTuple) =
            toName(value)
                    ?.let { nameToMacroString(it) }
                    ?: "missing_name"

    private fun nameToMacroString(name: OtpErlangAtom) = inspectAsFunction(name)

    private fun nameToMacroString(name: OtpErlangObject) =
            when (name) {
                is OtpErlangAtom -> nameToMacroString(name)
                else -> "unknown_name"
            }

    private fun toDefinitions(value: OtpErlangTuple): OtpErlangObject? = value.elementAt(1)

    private fun toMacroString(value: OtpErlangTuple): MacroString {
        val nameMacroString = nameMacroString(value)

        return toMacroString(value, nameMacroString)
    }

    private fun toMacroString(value: OtpErlangTuple, nameMacroString: MacroString): MacroString =
        toDefinitions(value)
                ?.let { definitionsToMacroString(it, nameMacroString) }
                ?: "missing_definitions"

    private fun toName(value: OtpErlangTuple) =
            toNameArity(value)
                    ?.let { nameArityToName(it) }

    private fun toNameArity(value: OtpErlangTuple): OtpErlangObject? = value.elementAt(0)
}
