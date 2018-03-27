package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple

private const val TAG = "tuple"

class Tuple(term: OtpErlangTuple): Node(term) {
    val elements by lazy { term.elements().drop(2).mapNotNull { Node.from(it) } }

    override fun toMacroString(): String = "{${elements.joinToString(", ", transform = Node::toMacroString)}}"

    companion object {
        fun from(term: OtpErlangObject) = ifTag(term, TAG, ::Tuple)
    }
}
