package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.bin_element

import com.ericsson.otp.erlang.OtpErlangBinary
import com.ericsson.otp.erlang.OtpErlangObject
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.AbstractCodeString
import org.elixir_lang.beam.term.inspect

object Pattern {
    fun toElixirString(term: OtpErlangObject): OtpErlangBinary? = AbstractCodeString.toElixirString(term)

    /**
     * Elixir does not have `<<'charlist'>>`, but `<<"string">`
     */
    fun toMacroString(term: OtpErlangObject): String =
            toElixirString(term)
                    ?.let { inspect(it) }
                    ?: AbstractCode.toMacroString(term)
}
