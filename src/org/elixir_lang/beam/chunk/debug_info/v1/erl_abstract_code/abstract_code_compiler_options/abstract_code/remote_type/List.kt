package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.remote_type

import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangObject
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.*
import org.elixir_lang.code.Identifier.inspectAsFunction

object List {
    fun toString(list: OtpErlangObject): String =
            when (list) {
                is OtpErlangList -> toString(list)
                else -> "unknown_remote_type_list"
            }

    private fun argumentsString(list: OtpErlangList) =
            toArguments(list)
                    ?.let { argumentsToString(it) }
                    ?: "unknown_arguments"

    private fun argumentsToString(arguments: OtpErlangObject) =
            Sequence
                    .toMacroStringDeclaredScope(arguments, Scope.EMPTY, "", Separator(", ", group = true), "")
                    .macroString
                    .string

    private fun moduleString(list: OtpErlangList): String =
            toModule(list)
                    ?.let { AbstractCode.toString(it) }
                    ?: "missing_remote_type_module"

    private fun nameString(list: OtpErlangList) =
            toName(list)
                    ?.let { nameToString(it) }
                    ?: "missing_remote_type_name"

    private fun nameToString(name: OtpErlangObject) =
            Atom.toElixirAtom(name)
                    ?.let { inspectAsFunction(it) }
                    ?: "unknown_remote_type_name"

    private fun toArguments(list: OtpErlangList): OtpErlangObject? = list.elementAt(2)

    private fun toString(list: OtpErlangList): String {
        val moduleString = moduleString(list)
        val nameString = nameString(list)
        val argumentsString = argumentsString(list)

        return "$moduleString.$nameString($argumentsString)"
    }

    private fun toModule(list: OtpErlangList): OtpErlangObject? = list.elementAt(0)
    private fun toName(list: OtpErlangList): OtpErlangObject? = list.elementAt(1)
}
