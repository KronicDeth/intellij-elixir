package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangAtom
import com.ericsson.otp.erlang.OtpErlangLong
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple

fun Node?.toMacroString() = this?.toMacroString() ?: "?"

abstract class Node(term: OtpErlangTuple): ToMacroString {
    val line by lazy { (term.elementAt(1) as? OtpErlangLong)?.longValue() }

    abstract override fun toMacroString(): String

    companion object {
        inline fun <T : Node> ifTag(term: OtpErlangObject, tag: String, ifTrue: (OtpErlangTuple) -> T?): T? =
            when (term) {
                is OtpErlangTuple -> ifTag(term, tag, ifTrue)
                else -> null
            }

        inline fun <T: Node> ifTag(term: OtpErlangTuple, tag: String, ifTrue: (OtpErlangTuple) -> T?): T? =
            (term.elementAt(0) as? OtpErlangAtom)?.let { actualTag ->
                if (actualTag.atomValue() == tag) {
                    ifTrue(term)
                } else {
                    null
                }
            }

        fun from(term: OtpErlangObject): Node? =
            when (term) {
                is OtpErlangTuple -> from(term)
                else -> null
            }

        fun from(term: OtpErlangTuple): Node? =
                Atom.from(term) ?:
                Bin.from(term) ?:
                Call.from(term) ?:
                Char.from(term) ?:
                Cons.from(term) ?:
                Integer.from(term) ?:
                Map.from(term) ?:
                Match.from(term) ?:
                Nil.from(term) ?:
                Op.from(term) ?:
                Record.from(term) ?:
                RecordField.from(term) ?:
                RecordIndex.from(term) ?:
                Remote.from(term) ?:
                Tuple.from(term) ?:
                Var.from(term) ?:
                TODO()
//
//                Var.from(term)
    }
}
