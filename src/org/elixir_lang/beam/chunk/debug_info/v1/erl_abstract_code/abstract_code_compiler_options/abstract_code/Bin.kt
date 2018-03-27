package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple

private const val TAG = "bin"

class Bin(term: OtpErlangTuple): Node(term) {
    val binElements by lazy {
        (term.elementAt(2) as? OtpErlangList)?.filterIsInstance<OtpErlangTuple>()?.mapNotNull { BinElement.from(it) }
    }

    override fun toMacroString(): String =
        "<<${binElements?.joinToString(", ", transform = BinElement::toMacroString) ?: "?"}>>"

    companion object {
        fun from(term: OtpErlangObject): Bin? = ifTag(term, TAG, ::Bin)
    }
}
