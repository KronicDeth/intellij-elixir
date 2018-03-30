package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangLong
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode

object Integer {
    fun ifToMacroString(term: OtpErlangObject?): String? = AbstractCode.ifTag(term, TAG) { toMacroString(it) }

    fun toMacroString(term: OtpErlangObject): String =
            when (term) {
                is OtpErlangTuple -> toMacroString(term)
                else -> "unknown_integer"
            }

    fun toMacroString(term: OtpErlangTuple): String =
            toInteger(term)
                    ?.let { integerToMacroString(it) }
    ?: "missing_integer"

    private const val TAG = "integer"

    private fun integerToMacroString(term: OtpErlangLong): String = term.intValue().toString()

    private fun integerToMacroString(term: OtpErlangObject): String =
            when (term) {
                is OtpErlangLong -> integerToMacroString(term)
                else -> "unknown_integer"
            }

    private fun toInteger(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(2)
}
