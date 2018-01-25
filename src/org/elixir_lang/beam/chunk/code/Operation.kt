package org.elixir_lang.beam.chunk.code

import com.intellij.openapi.util.component1
import com.intellij.openapi.util.component2
import org.elixir_lang.beam.chunk.Chunk.unsignedByte
import org.elixir_lang.beam.chunk.code.operation.Argument
import org.elixir_lang.beam.chunk.code.operation.Code
import org.elixir_lang.beam.chunk.code.operation.codeByNumber
import org.elixir_lang.beam.term.Label
import org.elixir_lang.beam.term.Literal
import org.elixir_lang.beam.term.Term

/**
 * https://github.com/erlang/otp/blob/OTP-20.2.2/lib/compiler/src/genop.tab
 */
data class Operation(val code: Code, val termList: List<Term>) {
    private fun argumentsAs(termToString: (Term, Argument) -> String): String {
        val argumentNameToType = code.argumentNameToType

        val argumentList = if (argumentNameToType != null && argumentNameToType.size == termList.size) {
            termList.mapIndexed { index, term ->
                val (argumentName, type) = argumentNameToType[index]

                "$argumentName: ${termToString(term, type)}"
            }
        } else {
            termList
        }

        return argumentList.joinToString(", ")
    }

    private fun argumentsAsAssembly(): String = argumentsAs { term, type -> termToType(term, type) }
    fun argumentsAsTerms(): String = argumentsAs { term, _ -> term.toString() }
    fun assembly(): String {
        val function = code.function

        return when (function) {
            "allocate" -> {
                val (wordsOfStack, liveXRegisterCount) = termList

                if (wordsOfStack is Literal && wordsOfStack.index == 0 &&
                        liveXRegisterCount is Literal && liveXRegisterCount.index == 0) {
                    // https://github.com/elixir-lang/elixir/issues/7258
                    "save_cp()"
                } else {
                    "$function(${argumentsAsAssembly()})"
                }
            }
            "deallocate" -> {
                val (wordsOfStack) = termList

                if (wordsOfStack is Literal && wordsOfStack.index == 0) {
                    // https://github.com/elixir-lang/elixir/issues/7258
                    "restore_cp()"
                } else {
                    "$function(${argumentsAsAssembly()})"
                }
            }
            "label", "line" -> "$function(${(termList[0] as Literal).index})"
            else -> "$function(${argumentsAsAssembly()})"
        }
    }

    private fun termToInteger(term: Term): String =
            when (term) {
                is Label ->
                    term.index.toString()
                is Literal ->
                    term.index.toString()
                else ->
                    term.toString()
            }

    private fun termToType(term: Term, type: Argument): String =
        when (type) {
            Argument.INTEGER -> termToInteger(term)
            Argument.TERM -> term.toString()
        }

    override fun toString(): String = "${code.function}(${argumentsAsTerms()})"

    companion object {
        fun from(data: ByteArray, offset: Int, literalFloat: Boolean): Pair<Operation, Int> {
            var internalOffset = offset

            val (opcode, opcodeByteCount) = unsignedByte(data[internalOffset])
            internalOffset += opcodeByteCount

            val code = codeByNumber[opcode]

            return if (code != null) {
                val termList = mutableListOf<Term>()

                repeat(code.arity) {
                    val (term, byteCount) = Term.from(data, internalOffset, literalFloat)
                    termList.add(term)
                    internalOffset += byteCount
                }

                Pair(Operation(code, termList), internalOffset - offset)
            } else {
                throw IllegalArgumentException("Unknown opcode ($opcode)")
            }
        }
    }
}
