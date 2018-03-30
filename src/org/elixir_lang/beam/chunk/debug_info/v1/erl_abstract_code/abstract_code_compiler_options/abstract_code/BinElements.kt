package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangBinary
import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangObject
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode

object BinElements {
    fun singleOrNull(term: OtpErlangObject): OtpErlangObject? = (term as? OtpErlangList)?.singleOrNull()

    fun toElixirString(term: OtpErlangObject): OtpErlangBinary? =
        singleOrNull(term)?.let { binElement ->
            BinElement.ifTo(binElement) { binElementTuple ->
                BinElement.toElixirString(binElementTuple)
            }
        }

    fun toMacroString(term: OtpErlangList): String =
            term.joinToString(", ") { AbstractCode.toMacroString(it) }

    fun toMacroString(term: OtpErlangObject): String =
            when (term) {
                is OtpErlangList -> toMacroString(term)
                else -> "unknown_bin_elements"
            }
}
