package org.elixir_lang.beam.chunk.code

import com.intellij.openapi.util.component1
import com.intellij.openapi.util.component2
import org.elixir_lang.beam.Cache
import org.elixir_lang.beam.chunk.Chunk.unsignedByte
import org.elixir_lang.beam.chunk.code.operation.Code
import org.elixir_lang.beam.chunk.code.operation.codeByNumber
import org.elixir_lang.beam.term.Label
import org.elixir_lang.beam.term.Literal
import org.elixir_lang.beam.term.Term

/**
 * https://github.com/erlang/otp/blob/OTP-20.2.2/lib/compiler/src/genop.tab
 */
data class Operation(val code: Code, val termList: List<Term>) {
    fun assembly(cache: Cache, options: org.elixir_lang.beam.chunk.Code.Options): String {
        val function = code.function

        return when (function) {
            "allocate" -> {
                val (wordsOfStack, liveXRegisterCount) = termList

                if (wordsOfStack is Literal && wordsOfStack.index == 0 &&
                        liveXRegisterCount is Literal && liveXRegisterCount.index == 0) {
                    // https://github.com/elixir-lang/elixir/issues/7258
                    "save_cp()"
                } else {
                    null
                }
            }
            "call_ext", "call_ext_only" -> {
                if (options.inline.imports) {
                    val index = 1
                    val valueAssembly = code.arguments[index].assembly(
                            termList[index],
                            cache,
                            options.copy(showArgumentNames = false)
                    )

                    "$function($valueAssembly)"
                } else {
                    null
                }
            }
            "deallocate" -> {
                val (wordsOfStack) = termList

                if (wordsOfStack is Literal && wordsOfStack.index == 0) {
                    // https://github.com/elixir-lang/elixir/issues/7258
                    "restore_cp()"
                } else {
                    null
                }
            }
            "label", "line" ->
                if (options.inline.integers) {
                    "$function(${(termList[0] as Literal).index})"
                } else {
                    // literals of label and line aren't literal references, so don't inline them using inlineLiterals
                    "$function(${argumentsAssembly(cache, options.copy(inline = options.inline.copy(literals = false)))})"
                }
            else -> null
        } ?: "$function(${argumentsAssembly(cache, options)})"
    }

    private fun argumentsAssembly(cache: Cache, options: org.elixir_lang.beam.chunk.Code.Options): String =
            code.arguments.zip(termList).joinToString(", ") { (argument, term) ->
                argument.assembly(term, cache, options)
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

    companion object {
        fun from(data: ByteArray, offset: Int, literalFloat: Boolean): Pair<Operation, Int> {
            var internalOffset = offset

            val (opcode, opcodeByteCount) = unsignedByte(data[internalOffset])
            internalOffset += opcodeByteCount

            val code = codeByNumber[opcode]

            return if (code != null) {
                val termList = mutableListOf<Term>()

                repeat(code.arguments.size) {
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
