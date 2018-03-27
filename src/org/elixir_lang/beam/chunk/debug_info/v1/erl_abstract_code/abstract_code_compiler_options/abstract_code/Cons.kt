package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple

private const val TAG = "cons"

class Cons(term: OtpErlangTuple): Node(term) {
    val head by lazy { term.elementAt(2)?.let { Node.from(it) } }
    val tail by lazy { term.elementAt(3)?.let { Node.from(it) } }

    override fun toMacroString(): String = "[${head.toMacroString()} | ${tail.toMacroString()}]"

    companion object {
        fun from(term: OtpErlangObject): Cons? = ifTag(term, TAG, ::Cons)
    }
}
