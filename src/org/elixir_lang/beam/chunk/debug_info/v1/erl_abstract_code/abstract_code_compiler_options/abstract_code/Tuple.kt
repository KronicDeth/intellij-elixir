package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode

object Tuple {
    fun ifToMacroString(term: OtpErlangObject?): String? = AbstractCode.ifTag(term, TAG) { toMacroString(it) }

    fun toMacroString(term: OtpErlangObject): String =
            when (term) {
                is OtpErlangTuple -> toMacroString(term)
                else -> "unknown_tuple"
            }

    fun toMacroString(term: OtpErlangTuple): String {
        val elementsMacroString = elementsMacroString(term)

        return "{$elementsMacroString}"
    }

    private const val TAG = "tuple"

    private fun elementsMacroString(term: OtpErlangTuple): String =
            toElements(term)
                    ?.let { elementsToMacroString(it) }
                    ?: "missing_elements"

    private fun elementsToMacroString(term: OtpErlangList): String =
            term.joinToString(", ") { AbstractCode.toMacroString(it) }

    private fun elementsToMacroString(term: OtpErlangObject): String =
            when (term) {
                is OtpErlangList -> elementsToMacroString(term)
                else -> "unknown_elements"
            }

    private fun toElements(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(2)
}
