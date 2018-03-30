package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode


object Cons {
    fun ifToMacroString(term: OtpErlangObject?): String? = AbstractCode.ifTag(term, TAG) { toMacroString(it) }

    fun toMacroString(term: OtpErlangObject): String =
            when (term) {
                is OtpErlangTuple -> toMacroString(term)
                else -> "unknown_cons"
            }

    fun toMacroString(term: OtpErlangTuple): String {
        val headMacroString = headMacroString(term)
        val tailMacroString = tailMacroString(term)

        return "[$headMacroString | $tailMacroString]"
    }

    private const val TAG = "cons"

    private fun headMacroString(term: OtpErlangTuple): String =
            toHead(term)
                    ?.let { headToMacroString(it) }
                    ?: "missing_head"

    private fun headToMacroString(term: OtpErlangObject) = AbstractCode.toMacroString(term)

    private fun tailMacroString(term: OtpErlangTuple): String =
            toTail(term)
                    ?.let { tailToMacroString(it) }
                    ?: "missing_tail"

    private fun tailToMacroString(term: OtpErlangObject) = AbstractCode.toMacroString(term)
    private fun toHead(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(2)
    private fun toTail(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(3)
}
