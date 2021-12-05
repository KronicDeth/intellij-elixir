package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.type

import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Scope

object MapField {
    fun toString(type: OtpErlangTuple, necessity: String) =
            toPair(type)
                    ?.let { pairToString(it, necessity) }
                    ?: AbstractCode.missing("pair", "type map field pair", type)

    private fun keyString(pair: OtpErlangList) =
            pairToKey(pair)
                    ?.let { keyToString(it) }
                    ?: AbstractCode.missing("key", "type map field key", pair)

    private fun keyToString(key: OtpErlangObject): String = AbstractCode.toString(key)

    private fun pairToKey(pair: OtpErlangList): OtpErlangObject? = pair.elementAt(0)

    private fun pairToString(pair: OtpErlangList, necessity: String): String {
        val keyString = keyString(pair)
        val valueString = valueString(pair)

        return "$necessity($keyString) => $valueString"
    }

    private fun pairToString(pair: OtpErlangObject, necessity: String) =
            when (pair) {
                is OtpErlangList -> pairToString(pair, necessity)
                else -> AbstractCode.unknown("pair", "type map field pair", pair)
            }

    private fun pairToValue(pair: OtpErlangList): OtpErlangObject? = pair.elementAt(1)
    private fun toPair(type: OtpErlangTuple): OtpErlangObject? = type.elementAt(3)

    private fun valueString(pair: OtpErlangList): String =
            pairToValue(pair)
                    ?.let { valueToString(it) }
                    ?: AbstractCode.missing("value", "type mape field value", pair)

    private fun valueToString(key: OtpErlangObject): String = AbstractCode.toString(key)
}
