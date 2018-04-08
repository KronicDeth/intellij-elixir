package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.type

import com.ericsson.otp.erlang.OtpErlangAtom
import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.MacroString
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Scope
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Type.ifSubtypeTo
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Type.subtypeMacroString

object Builtin {
    fun ifToMacroString(type: OtpErlangTuple): MacroString? = ifSubtypeTo(type, SUBTYPE_SET) { toMacroString(type) }

    private val SUBTYPE_SET = setOf(
            "any",
            "arity",
            "atom",
            "binary",
            "boolean",
            "function",
            "integer",
            "iodata",
            "iolist",
            "list",
            "map",
            "mfa",
            "module",
            "neg_integer",
            "nil",
            "no_return",
            "node",
            "non_neg_integer",
            "none",
            "nonempty_list",
            "nonempty_string",
            "pid",
            "port",
            "pos_integer",
            "reference",
            "string",
            "term",
            "timeout",
            "tuple"
    )

    private fun argumentsMaybeToMacroString(arguments: OtpErlangObject?) =
            arguments
                    ?.let { argumentsToMacroString(it) }
                    ?: "missing_arguments"

    private fun argumentToMacroString(argument: OtpErlangObject) =
            AbstractCode.toMacroStringDeclaredScope(argument, Scope.EMPTY).macroString

    private fun argumentsToMacroString(arguments: OtpErlangList) =
            arguments.joinToString(", ") { argumentToMacroString(it) }

    private fun argumentsToMacroString(arguments: OtpErlangObject) =
            when (arguments) {
                is OtpErlangList -> argumentsToMacroString(arguments)
                else -> "unknown_arguments"
            }

    private fun toArguments(type: OtpErlangTuple): OtpErlangObject? = type.elementAt(3)

    private fun toMacroString(type: OtpErlangTuple) =
        toArguments(type).let { arguments ->
            when (arguments) {
                is OtpErlangAtom -> toMacroString(type, arguments)
                else -> toMacroString(type, arguments)
            }
        }

    private fun toMacroString(type: OtpErlangTuple, arguments: OtpErlangAtom) =
        if (arguments.atomValue() == "any") {
            val subtypeMacroString = subtypeMacroString(type)

            when (subtypeMacroString) {
                "string" -> "charlist()"
                else -> "$subtypeMacroString()"
            }
        } else {
            "unknown_atom_arguments"
        }

    private fun toMacroString(type: OtpErlangTuple, arguments: OtpErlangObject?): MacroString {
        val subtypeMacroString = subtypeMacroString(type)
        val argumentsMacroString = argumentsMaybeToMacroString(arguments)

        return when (subtypeMacroString) {
            "list" -> "[$argumentsMacroString]"
            "map" -> "%{$argumentsMacroString}"
            "nil" -> "[]"
            "nonempty_list" -> "[$argumentsMacroString, ...]"
            "nonempty_string" -> "[char(), ...]"
            "string" -> "charlist($argumentsMacroString)"
            "tuple" -> "{$argumentsMacroString}"
            else -> "$subtypeMacroString($argumentsMacroString)"
        }
    }
}
