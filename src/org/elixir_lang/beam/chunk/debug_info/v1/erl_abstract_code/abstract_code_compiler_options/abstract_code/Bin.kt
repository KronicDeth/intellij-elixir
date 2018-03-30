package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode


object Bin {
    fun ifToMacroString(term: OtpErlangObject?): String? = AbstractCode.ifTag(term, TAG) { toMacroString(it) }

    fun toMacroString(term: OtpErlangTuple): String = "<<${binElementsMacroString(term)}>>"

    private const val TAG = "bin"

    private fun binElementsMacroString(term: OtpErlangTuple): String =
            (term.elementAt(2) as? OtpErlangList)?.joinToString(", ") { BinElement.toMacroString(it) } ?:
            "unknown_bin_elements"
}
