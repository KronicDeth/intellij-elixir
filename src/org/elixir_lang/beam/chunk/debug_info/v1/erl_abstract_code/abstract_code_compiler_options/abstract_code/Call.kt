package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangAtom
import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode.ifTag
import org.elixir_lang.code.Identifier.inspectAsFunction


object Call {
    fun ifToMacroStringDeclaredScope(term: OtpErlangObject): MacroStringDeclaredScope? =
            ifTag(term, TAG) { toMacroStringDeclaredScope(it) }

    fun toMacroStringDeclaredScope(term: OtpErlangTuple): MacroStringDeclaredScope {
        val macroString = toMacroString(term)

        return MacroStringDeclaredScope(macroString, Scope.EMPTY)
    }

    private const val TAG = "call"

    private fun anonymousFunctionCallToMacroString(name: OtpErlangTuple, term: OtpErlangTuple): MacroString {
        val nameMacroString = Var.toMacroStringDeclaredScope(name, Scope.EMPTY).macroString
        val argumentsMacroString = argumentsMacroString(term)

        return "$nameMacroString.($argumentsMacroString)"
    }

    private fun argumentsMacroString(term: OtpErlangTuple): String =
            toArguments(term)
                    ?.let { argumentsToMacroString(it) }
                    ?: "missing_arguments"

    private fun argumentsToMacroString(term: OtpErlangList): String =
            term.joinToString(", ") { AbstractCode.toMacroStringDeclaredScope(it, Scope.EMPTY).macroString }

    private fun argumentsToMacroString(term: OtpErlangObject): String =
        when (term) {
            is OtpErlangList -> argumentsToMacroString(term)
            else -> "unknown_arguments"
        }

    private fun namedFunctionCallToMacroString(term: OtpErlangTuple): MacroString {
        val nameMacroString = nameMacroString(term)
        val argumentsMacroString = argumentsMacroString(term)

        return "$nameMacroString($argumentsMacroString)"
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
            } ?: AbstractCode.toMacroStringDeclaredScope(term, Scope.EMPTY).macroString

    private fun toArguments(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(3)

    private fun toMacroString(term: OtpErlangTuple): String {
        val name = toName(term)

        return when {
            Var.`is`(name) -> anonymousFunctionCallToMacroString(name as OtpErlangTuple, term)
            else -> namedFunctionCallToMacroString(term)
        }
    }

    private fun toName(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(2)
}
