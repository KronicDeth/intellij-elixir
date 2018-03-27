package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple

private const val TAG = "nil"

class Nil(term: OtpErlangTuple): Node(term) {
    override fun toMacroString(): String = "[]"

    companion object {
        fun from(term: OtpErlangObject) = ifTag(term, TAG, ::Nil)
    }
}
