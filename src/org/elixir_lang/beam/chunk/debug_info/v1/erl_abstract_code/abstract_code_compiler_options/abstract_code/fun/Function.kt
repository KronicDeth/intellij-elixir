package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.`fun`

import com.ericsson.otp.erlang.OtpErlangAtom
import com.ericsson.otp.erlang.OtpErlangLong
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode.ifTag
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Integer
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.MacroString
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Scope
import org.elixir_lang.code.Identifier.inspectAsFunction

object Function {
    fun ifToMacroString(term: OtpErlangObject, scope: Scope): MacroString? =
            ifTag(term, TAG) { toMacroString(it, scope) }

    fun toMacroString(term: OtpErlangTuple, scope: Scope): MacroString {
        val string = when (val arity = term.arity()) {
            3 -> nameArityToMacroString(term.elementAt(1), term.elementAt(2))
            4 -> moduleNameArityToMacroString(term.elementAt(1), term.elementAt(2), term.elementAt(3), scope)
            else -> "fun_function_unknown_arity($arity)"
        }

        return MacroString(string, doBlock = false)
    }

    private const val TAG = "function"

    private fun arityToString(arity: OtpErlangObject) =
            when (arity) {
                is OtpErlangLong -> arity.longValue().toString()
                else -> AbstractCode.toString(arity)
            }

    private fun moduleNameArityToMacroString(
            module: OtpErlangObject,
            name: OtpErlangObject,
            arity: OtpErlangObject,
            scope: Scope
    ): String {
        val moduleString = AbstractCode.toString(module, scope)
        val nameString = nameToString(name)
        val arityString = arityToString(arity)

        return "&$moduleString.$nameString/$arityString"
    }

    private fun nameArityToMacroString(name: OtpErlangObject, arity: OtpErlangObject): String {
        val nameString = nameToString(name)
        val arityString = arityToString(arity)

        return "&$nameString/$arityString"
    }

    private fun nameToString(term: OtpErlangObject): String =
            when (term) {
                is OtpErlangAtom -> inspectAsFunction(term, true)
                else -> "fun_unknown_name"
            }
}
