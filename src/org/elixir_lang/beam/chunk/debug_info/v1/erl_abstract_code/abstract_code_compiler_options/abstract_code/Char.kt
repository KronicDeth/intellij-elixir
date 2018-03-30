package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangLong
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode


object Char {
    fun ifToMacroString(term: OtpErlangObject?): String? = AbstractCode.ifTag(term, TAG) { toMacroString(it) }

    fun toMacroString(term: OtpErlangObject): String =
            when (term) {
                is OtpErlangTuple -> toMacroString(term)
                else -> "unknown_char"
            }

    fun toMacroString(term: OtpErlangTuple): String =
            toCodePoint(term)
                    ?.let { codePointToMacroString(it) }
                    ?: "missing_code_point"

    private const val TAG = "char"

    private fun codePointToMacroString(term: OtpErlangLong): String =
            StringBuilder().append('?').append(term.intValue()).toString()

    private fun codePointToMacroString(term: OtpErlangObject): String =
            when (term) {
                is OtpErlangLong -> codePointToMacroString(term)
                else -> "unknown_code_point"
            }

    private fun toCodePoint(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(2)
}
