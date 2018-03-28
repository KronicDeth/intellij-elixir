package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple

private const val TAG = "tuple"

class Tuple(term: OtpErlangTuple): Node(term) {
    val elements by lazy {
        (term.elementAt(2) as? OtpErlangList)?.mapNotNull { Node.from(it) } ?:
        emptyList()
    }

    override fun toMacroString(): String = elementsToMacroString(elements)

    companion object {
        fun elementsToMacroString(elements: List<Node>): kotlin.String =
                "{${elements.joinToString(", ", transform = Node::toMacroString)}}"

        fun from(term: OtpErlangObject) = ifTag(term, TAG, ::Tuple)
    }
}
