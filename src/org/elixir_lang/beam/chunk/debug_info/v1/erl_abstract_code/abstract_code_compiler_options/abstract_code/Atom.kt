package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode
import org.elixir_lang.beam.term.inspect

object Atom {
    private const val TAG = "atom"

    fun <T> ifTo(term: OtpErlangObject?, ifTrue: (OtpErlangTuple) -> T): T? = AbstractCode.ifTag(term, TAG, ifTrue)

    fun ifToMacroString(term: OtpErlangObject?): String? = ifTo(term) { toMacroString(it) }

    fun toAtom(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(2)

    fun toMacroString(term: OtpErlangTuple): String =
            toAtom(term)
                    ?.let { inspect(it) }
                    ?: ":unknown_atom"

    fun toMacroString(term: OtpErlangObject?): String =
            when (term) {
                is OtpErlangTuple -> toMacroString(term)
                else -> ":unknown_atom"
            }
}
