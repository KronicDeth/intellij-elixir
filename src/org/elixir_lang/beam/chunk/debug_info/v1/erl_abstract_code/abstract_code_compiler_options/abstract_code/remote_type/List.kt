package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.remote_type

import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangObject
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Atom
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.MacroString
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Scope
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Sequence
import org.elixir_lang.code.Identifier.inspectAsFunction

object List {
    fun toMacroString(list: OtpErlangObject): MacroString =
            when (list) {
                is OtpErlangList -> toMacroString(list)
                else -> "unknown_remote_type_list"
            }

    private fun argumentsMacroString(list: OtpErlangList) =
            toArguments(list)
                    ?.let { argumentsToMacroString(it) }
                    ?: "unknown_arguments"

    private fun argumentsToMacroString(arguments: OtpErlangObject) =
            Sequence.toMacroStringDeclaredScope(arguments, Scope.EMPTY, ", ").macroString

    private fun moduleMacroString(list: OtpErlangList) =
            toModule(list)
                    ?.let { AbstractCode.toMacroStringDeclaredScope(it, Scope.EMPTY).macroString }
                    ?: "missing_remote_type_module"

    private fun nameMacroString(list: OtpErlangList) =
            toName(list)
                    ?.let { nameToMacroString(it) }
                    ?: "missing_remote_type_name"

    private fun nameToMacroString(name: OtpErlangObject) =
            Atom.toElixirAtom(name)
                    ?.let { inspectAsFunction(it) }
                    ?: "unknown_remote_type_name"

    private fun toArguments(list: OtpErlangList): OtpErlangObject? = list.elementAt(2)

    private fun toMacroString(list: OtpErlangList): MacroString {
        val moduleMacroString = moduleMacroString(list)
        val nameMacroString = nameMacroString(list)
        val argumentsMacroString = argumentsMacroString(list)

        return "$moduleMacroString.$nameMacroString($argumentsMacroString)"
    }

    private fun toModule(list: OtpErlangList): OtpErlangObject? = list.elementAt(0)
    private fun toName(list: OtpErlangList): OtpErlangObject? = list.elementAt(1)
}
