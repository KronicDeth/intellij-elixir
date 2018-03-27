package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangLong
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple

private const val TAG = "integer"

class Integer(term: OtpErlangTuple): Node(term) {
    val integer by lazy { (term.elementAt(2) as OtpErlangLong).intValue() }

    override fun toMacroString(): String = integer.toString()

    companion object {
        fun from(term: OtpErlangObject) = ifTag(term, TAG, ::Integer)
    }
}
