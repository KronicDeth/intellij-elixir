package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.`fun`

import com.ericsson.otp.erlang.OtpErlangAtom
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode.ifTag
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Atom
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.MacroString
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Scope
import org.elixir_lang.code.Identifier

object Function {
    fun ifToMacroString(term: OtpErlangObject, scope: Scope): MacroString? =
            ifTag(term, TAG) { toMacroString(it, scope) }

    fun toMacroString(term: OtpErlangTuple, scope: Scope): MacroString {
        val arity = term.arity()

        return when (arity) {
            3 -> nameArityToMacroString(term.elementAt(1), term.elementAt(2), scope)
            4 -> moduleNameArityToMacroString(term.elementAt(1), term.elementAt(2), term.elementAt(3), scope)
            else -> "fun_function_unknown_arity($arity)"
        }
    }

    private const val TAG = "function"

    private fun moduleNameArityToMacroString(
            module: OtpErlangObject,
            name: OtpErlangObject,
            arity: OtpErlangObject,
            scope: Scope
    ): MacroString {
        val moduleMacroString = AbstractCode.toMacroStringDeclaredScope(module, scope).macroString
        val nameMacroString = nameToMacroString(name)
        val arityMacroString = AbstractCode.toMacroStringDeclaredScope(arity, scope).macroString

        return "&$moduleMacroString.$nameMacroString/$arityMacroString"
    }

    private fun nameArityToMacroString(name: OtpErlangObject, arity: OtpErlangObject, scope: Scope): MacroString {
        val nameMacroString = nameToMacroString(name)
        val arityMacroString = AbstractCode.toMacroStringDeclaredScope(arity, scope).macroString

        return "&$nameMacroString/$arityMacroString"
    }

    private fun nameToMacroString(term: OtpErlangObject): String =
            Atom.ifTo(term) {
                Atom.toAtom(it)?.let { atom ->
                    if (atom is OtpErlangAtom) {
                        Identifier.inspectAsFunction(atom)
                    } else {
                        null
                    }
                } ?: "unknown_function"
            } ?: AbstractCode.toMacroStringDeclaredScope(term, Scope.EMPTY).macroString
}