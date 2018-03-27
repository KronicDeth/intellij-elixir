package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangLong
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple

private const val TAG = "char"

class Char(term: OtpErlangTuple): Node(term) {
    val codePoint by lazy { (term.elementAt(2) as? OtpErlangLong)?.intValue() ?: 0xFFFD }

    override fun toMacroString(): String = StringBuilder().append('?').appendCodePoint(codePoint).toString()

    companion object {
        fun from(term: OtpErlangObject) = ifTag(term, TAG, ::Char)
    }
}
