package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.type

import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.MacroString
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Scope

object MapField {
    fun toMacroString(type: OtpErlangTuple, necessity: MacroString) =
            toPair(type)
                    ?.let { pairToMacroString(it, necessity) }
                    ?: "missing_pair"

    private fun keyMacroString(pair: OtpErlangList) =
            pairToKey(pair)
                    ?.let { keyToMacroString(it) }
                    ?: "missing_key"

    private fun keyToMacroString(key: OtpErlangObject) =
            AbstractCode.toMacroStringDeclaredScope(key, Scope.EMPTY).macroString

    private fun pairToKey(pair: OtpErlangList): OtpErlangObject? = pair.elementAt(0)

    private fun pairToMacroString(pair: OtpErlangList, necessity: MacroString): MacroString {
        val keyMacroString = keyMacroString(pair)
        val valueMacroString = valueMacroString(pair)

        return "$necessity($keyMacroString) => $valueMacroString"
    }

    private fun pairToMacroString(pair: OtpErlangObject, necessity: MacroString) =
            when (pair) {
                is OtpErlangList -> pairToMacroString(pair, necessity)
                else -> "unknown_pair"
            }

    private fun pairToValue(pair: OtpErlangList): OtpErlangObject? = pair.elementAt(1)
    private fun toPair(type: OtpErlangTuple): OtpErlangObject? = type.elementAt(3)

    private fun valueMacroString(pair: OtpErlangList) =
            pairToValue(pair)
                    ?.let { valueToMacroString(it) }
                    ?: "missing_value"

    private fun valueToMacroString(key: OtpErlangObject) =
            AbstractCode.toMacroStringDeclaredScope(key, Scope.EMPTY).macroString
}
