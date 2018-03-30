package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode
import org.elixir_lang.beam.term.inspect


object Bin {
    fun ifToMacroString(term: OtpErlangObject?): String? = AbstractCode.ifTag(term, TAG) { toMacroString(it) }

    fun toMacroString(term: OtpErlangTuple): String {
        val binElements = toBinElements(term)

        return if (binElements != null) {
            BinElements.toElixirString(binElements)?.let(::inspect)
                    ?: "<<${BinElements.toMacroString(binElements)}>>"
        } else {
            "missing_bin_elements"
        }
    }

    private const val TAG = "bin"

    private fun toBinElements(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(2)

    private fun binElementsMacroString(term: OtpErlangTuple): String =
            toBinElements(term)
                    ?.let { BinElements.toMacroString(it) }
                    ?: "missing_bin_elements"
}
