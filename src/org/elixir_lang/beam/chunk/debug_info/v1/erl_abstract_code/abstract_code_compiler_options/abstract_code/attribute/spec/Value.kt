package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.attribute.spec

import com.ericsson.otp.erlang.OtpErlangAtom
import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.MacroString
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.attribute.spec.value.Definition
import org.elixir_lang.code.Identifier.inspectAsFunction

object Value {
    fun toMacroStrings(value: OtpErlangObject): List<MacroString> =
            when (value) {
                is OtpErlangTuple -> toMacroStrings(value)
                else -> emptyList()
            }

    private fun definitionsToMacroStrings(definitions: OtpErlangList, nameMacroString: MacroString) =
        definitions.map {
            Definition.toMacroString(it, nameMacroString)
        }

    private fun definitionsToMacroStrings(definitions: OtpErlangObject, nameMacroString: MacroString) =
            when (definitions) {
                is OtpErlangList -> definitionsToMacroStrings(definitions, nameMacroString)
                else -> emptyList()
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

    private fun toMacroStrings(value: OtpErlangTuple) =
            nameMacroString(value).let {
                toMacroStrings(value, it)
            }

    private fun toMacroStrings(value: OtpErlangTuple, nameMacroString: MacroString) =
            toDefinitions(value)
                    ?.let { definitionsToMacroStrings(it, nameMacroString) }
                    ?: emptyList()

    private fun toName(value: OtpErlangTuple) =
            toNameArity(value)
                    ?.let { nameArityToName(it) }

    private fun toNameArity(value: OtpErlangTuple): OtpErlangObject? = value.elementAt(0)
}
