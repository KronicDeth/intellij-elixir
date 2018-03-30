package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.comprehension

import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangObject

object Qualifiers {
    fun toMacroString(term: OtpErlangList): String = term.joinToString(", ") { Qualifier.toMacroString(it) }

    fun toMacroString(term: OtpErlangObject): String =
            when (term) {
                is OtpErlangList -> toMacroString(term)
                else -> "unknown_qualifiers"
            }
}
