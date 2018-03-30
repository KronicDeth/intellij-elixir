package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.clause

import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangObject
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode

object Body {
    fun toMacroString(term: OtpErlangList) =
            term.joinToString("\n") { AbstractCode.toMacroString(it) }

    fun toMacroString(term: OtpErlangObject?) =
            when (term) {
                is OtpErlangList -> toMacroString(term)
                else -> "..."
            }
}
