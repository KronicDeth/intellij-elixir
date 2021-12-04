package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.type

import com.ericsson.otp.erlang.OtpErlangAtom
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.*
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Type.ifSubtypeTo
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Type.subtypeString

object Builtin {
    fun ifToString(type: OtpErlangTuple): String? = ifSubtypeTo(type, SUBTYPE_SET) { toString(type) }

    private val SUBTYPE_SET = setOf(
            "any",
            "arity",
            "atom",
            "binary",
            "bitstring",
            "boolean",
            "byte",
            "char",
            "float",
            "function",
            "integer",
            "iodata",
            "iolist",
            "list",
            "map",
            "maybe_improper_list",
            "mfa",
            "module",
            "neg_integer",
            "nil",
            "no_return",
            "node",
            "nonempty_improper_list",
            "nonempty_maybe_improper_list",
            "non_neg_integer",
            "none",
            "nonempty_list",
            "nonempty_string",
            "number",
            "pid",
            "port",
            "pos_integer",
            "reference",
            "string",
            "term",
            "timeout",
            "tuple"
    )

    private fun argumentsMaybeToString(arguments: OtpErlangObject?): String =
            arguments
                    ?.let { argumentsToString(it) }
                    ?: "missing_arguments"

    private fun argumentsToString(arguments: OtpErlangObject): String =
            Sequence.toCommaSeparatedString(arguments, Scope.EMPTY)

    private fun toArguments(type: OtpErlangTuple): OtpErlangObject? = type.elementAt(3)

    private fun toString(type: OtpErlangTuple) =
        toArguments(type).let { arguments ->
            when (arguments) {
                is OtpErlangAtom -> toString(type, arguments)
                else -> toString(type, arguments)
            }
        }

    private fun toString(type: OtpErlangTuple, arguments: OtpErlangAtom): String =
        if (arguments.atomValue() == "any") {
            when (val subtypeMacroString = subtypeString(type)) {
                "string" -> "charlist()"
                else -> "$subtypeMacroString()"
            }
        } else {
            "unknown_atom_arguments"
        }

    private fun toString(type: OtpErlangTuple, arguments: OtpErlangObject?): String {
        val subtypeMacroString = subtypeString(type)
        val argumentsMacroString = argumentsMaybeToString(arguments)

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
