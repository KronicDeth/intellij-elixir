package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangAtom
import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode
import org.elixir_lang.code.Identifier.inspectAsFunction


object Call {
    fun ifToMacroString(term: OtpErlangObject?): String? = AbstractCode.ifTag(term, TAG) { toMacroString(it) }

    fun toMacroString(term: OtpErlangObject): String =
            when (term) {
                is OtpErlangTuple -> toMacroString(term)
                else -> "unknown_call"
            }

    fun toMacroString(term: OtpErlangTuple): String {
        val nameMacroString = nameMacroString(term)
        val argumentsMacroString = argumentsMacroString(term)

        return "$nameMacroString($argumentsMacroString)"
    }

    private const val TAG = "call"

    private fun argumentsMacroString(term: OtpErlangTuple): String =
            toArguments(term)
                    ?.let { argumentsToMacroString(it) }
                    ?: "missing_arguments"

    private fun argumentsToMacroString(term: OtpErlangList): String =
            term.joinToString(", ") { AbstractCode.toMacroString(it) }

    private fun argumentsToMacroString(term: OtpErlangObject): String =
        when (term) {
            is OtpErlangList -> argumentsToMacroString(term)
            else -> "unknown_arguments"
        }

    private fun nameMacroString(term: OtpErlangTuple): String =
            toName(term)
                    ?.let { nameToMacroString(it) }
                    ?: "missing_name"

    private fun nameToMacroString(term: OtpErlangObject): String =
            Atom.ifTo(term) {
                Atom.toAtom(it)?.let { atom ->
                    if (atom is OtpErlangAtom) {
                        inspectAsFunction(atom)
                    } else {
                        null
                    }
                } ?: "unknown_function"
            } ?: AbstractCode.toMacroString(term)

    private fun toArguments(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(3)
    private fun toName(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(2)
}
