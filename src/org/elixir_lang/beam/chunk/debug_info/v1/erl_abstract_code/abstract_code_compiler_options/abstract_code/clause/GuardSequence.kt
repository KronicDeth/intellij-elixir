package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.clause

import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangObject

object GuardSequence {
    fun guardsMacroString(guardSequence: OtpErlangList): String =
            guardSequence.joinToString(" or ") { guard ->
                Guard.toMacroString(guard)
            }

    fun guardsMacroString(guardSequence: OtpErlangObject): String =
            when (guardSequence) {
                is OtpErlangList -> guardsMacroString(guardSequence)
                else -> "unknown_guard_sequence"
            }

    fun toMacroString(term: OtpErlangList): String =
        if (term.arity() > 0) {
            val guardsMacroString = guardsMacroString(term)

            " when $guardsMacroString"
        } else {
            ""
        }

    fun toMacroString(term: OtpErlangObject?): String =
        when (term) {
            is OtpErlangList -> toMacroString(term)
            else -> " when unknown_guard_sequence"
        }
}
