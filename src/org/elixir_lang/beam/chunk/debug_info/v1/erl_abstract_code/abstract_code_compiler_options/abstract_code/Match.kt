package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode

object Match {
    fun ifToMacroString(term: OtpErlangObject?): String? = AbstractCode.ifTag(term, TAG) { toMacroString(it) }

    fun toMacroString(term: OtpErlangTuple): String {
        val leftMacroString = leftMacroString(term)
        val rightMacroString = rightMacroString(term)

        return "$leftMacroString = $rightMacroString"
    }

    private const val TAG = "match"

    private fun leftMacroString(term: OtpErlangTuple): String =
            toLeft(term)
                    ?.let { AbstractCode.toMacroString(it) }
                    ?: "missing_left"

    private fun rightMacroString(term: OtpErlangTuple): String =
            toRight(term)
                    ?.let { AbstractCode.toMacroString(it) }
                    ?: "missing_right"

    private fun toLeft(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(2)
    private fun toRight(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(3)
}
