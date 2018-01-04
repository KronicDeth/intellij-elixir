package org.elixir_lang.beam.chunk.code

import com.intellij.openapi.util.component1
import com.intellij.openapi.util.component2
import org.elixir_lang.beam.chunk.Chunk.unsignedByte
import org.elixir_lang.beam.chunk.code.operation.Code
import org.elixir_lang.beam.chunk.code.operation.codeByNumber
import org.elixir_lang.beam.term.Term

/**
 * https://github.com/erlang/otp/blob/OTP-20.2.2/lib/compiler/src/genop.tab
 */
data class Operation(val code: Code, val termList: List<Term>) {
    override fun toString(): String {
        return "${code.function}(${argumentsString()})"
    }

    fun argumentsString(): String {
        val argumentNames = code.argumentNames

        return if (argumentNames != null) {
            if (argumentNames.size == termList.size) {
                termList.mapIndexed { index, term -> "${argumentNames[index]}: $term" }.joinToString(", ")
            } else {
                termList.joinToString(", ")
            }
        } else {
            termList.joinToString(", ")
        }
    }

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
