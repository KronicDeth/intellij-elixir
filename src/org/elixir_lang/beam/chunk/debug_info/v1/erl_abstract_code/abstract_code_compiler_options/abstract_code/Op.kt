package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangAtom
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.code.Identifier.inspectAsFunction

private const val TAG = "op"

class Op(term: OtpErlangTuple): Node(term) {
    val operator by lazy { term.elementAt(2) as? OtpErlangAtom }
    val operands by lazy {
        term.elements().drop(3).mapNotNull { Node.from(it) }
    }

    override fun toMacroString(): String =
        when (operands.size) {
            1 -> "${operatorToMacroString()}${operands[0].toMacroString()}"
            2 -> {
                val leftMacroString = operands[0].toMacroString()
                val rightMacroString = operands[1].toMacroString()

                val operatorMacroString = operator?.let {
                    val operatorAtomValue = it.atomValue()

                    when (operatorAtomValue) {
                        "band" -> "&&&"
                        "bor" -> "|||"
                        "bsl" -> "<<<"
                        "bsr" -> ">>>"
                        "bxor" -> "^^^"
                        else -> operatorAtomValue
                    }
                } ?: operatorToMacroString()

                "$leftMacroString $operatorMacroString $rightMacroString"
            }
            else -> TODO()
        }

    private fun operatorToMacroString(): String = operator?.let { inspectAsFunction(it) } ?: "?"

    companion object {
        fun from(term: OtpErlangObject) = ifTag(term, TAG, ::Op)
    }
}
