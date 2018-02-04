package org.elixir_lang.beam.chunk.code

import com.intellij.openapi.util.component1
import com.intellij.openapi.util.component2
import org.elixir_lang.beam.Cache
import org.elixir_lang.beam.chunk.Chunk.unsignedByte
import org.elixir_lang.beam.chunk.code.operation.Code
import org.elixir_lang.beam.chunk.code.operation.code.Argument
import org.elixir_lang.beam.chunk.code.operation.code.reference
import org.elixir_lang.beam.chunk.code.operation.codeByNumber
import org.elixir_lang.beam.term.Atom
import org.elixir_lang.beam.term.Label
import org.elixir_lang.beam.term.Literal
import org.elixir_lang.beam.term.Term


const val BITS_PER_BYTE = 8

/**
 * https://github.com/erlang/otp/blob/OTP-20.2.2/lib/compiler/src/genop.tab
 */
data class Operation(val code: Code, val termList: List<Term>) {
    fun assembly(cache: Cache, options: org.elixir_lang.beam.chunk.Code.Options): String {

        return when (code) {
            Code.ALLOCATE -> {
                val (wordsOfStack, liveXRegisterCount) = termList

                if (wordsOfStack is Literal && wordsOfStack.index == 0 &&
                        liveXRegisterCount is Literal && liveXRegisterCount.index == 0) {
                    // https://github.com/elixir-lang/elixir/issues/7258
                    "save_cp()"
                } else {
                    null
                }
            }
            Code.BS_MATCH_STRING ->
                if (options.inline.strings) {
                    cache.strings?.pool?.let { pool ->
                        (termList[2] as? Literal)?.index?.let { bit_length ->
                            (termList[3] as? Literal)?.index?.let { start ->
                                val length = (bit_length + BITS_PER_BYTE - 1) / BITS_PER_BYTE
                                val end = start + length
                                val poolLength = pool.length

                                if (start <= poolLength && end <= poolLength) {
                                    val string = pool.substring(start, end).replace("'", "\'")

                                    val argumentsAssembly = argumentsAssembly(
                                            code.arguments.zip(termList).take(2),
                                            cache,
                                            options
                                    )

                                    "${code.function}($argumentsAssembly, string: '$string')"
                                } else {
                                    null
                                }
                            }
                        }
                    }
                } else {
                    null
                }
            Code.BS_PUT_STRING ->
                if (options.inline.strings) {
                    cache.strings?.pool?.let { pool ->
                        (termList[0] as? Literal)?.index?.let { length ->
                            (termList[1] as? Literal)?.index?.let { start ->
                                val end = start + length
                                val poolLength = pool.length

                                if (start <= poolLength && end <= poolLength) {
                                    val string = pool.substring(start, end).replace("'", "\'")

                                    "${code.function}('$string')"
                                } else {
                                    null
                                }
                            }
                        }
                    }
                } else {
                    null
                }
            Code.CALL_EXT, Code.CALL_EXT_ONLY ->
                if (options.inline.imports) {
                    val index = 1
                    val valueAssembly = code.arguments[index].assembly(
                            termList[index],
                            cache,
                            options.copy(showArgumentNames = false)
                    )

                    "${code.function}($valueAssembly)"
                } else {
                    null
                }
            Code.CALL_EXT_LAST ->
                if (options.inline.imports) {
                    val argumentsAssembly = argumentsAssembly(
                            code.arguments.zip(termList).drop(1),
                            cache,
                            options
                    )

                    "${code.function}($argumentsAssembly)"
                } else {
                    null
                }
            Code.CALL_ONLY ->
                if (options.inline.localCalls) {
                    labelReference(termList[1], cache)?.let { labelReference ->
                        "${code.function}($labelReference)"
                    }
                } else {
                    null
                }
            Code.DEALLOCATE -> {
                val (wordsOfStack) = termList

                if (wordsOfStack is Literal && wordsOfStack.index == 0) {
                    // https://github.com/elixir-lang/elixir/issues/7258
                    "restore_cp()"
                } else {
                    null
                }
            }
            Code.LABEL -> {
                val function = code.function

                if (options.inline.integers) {
                    "$function(${(termList[0] as Literal).index})"
                } else {
                    // literal of label isn't literal references, so don't inline them using inlineLiterals
                    "$function(${argumentsAssembly(cache, options.copy(inline = options.inline.copy(literals = false)))})"
                }
            }
            Code.LINE -> {
                val function = code.function

                when {
                    options.inline.lines -> {
                        cache.lines?.let { lines ->
                            (termList[0] as? Literal)?.index?.let { index ->
                                lines.lineReferenceList.getOrNull(index)?.let { lineReference ->
                                    lines.fileNameList.getOrNull(lineReference.fileNameIndex)?.let { fileName ->
                                        val line = lineReference.line

                                        "$function(file_name: \"$fileName\", line: $line)"
                                    }
                                }
                            }
                        }
                    }
                    options.inline.integers -> "$function(${(termList[0] as Literal).index})"
                    else ->
                        // literal of line isn't literal references, so don't inline them using inlineLiterals
                        "$function(${argumentsAssembly(cache, options.copy(inline = options.inline.copy(literals = false)))})"
                }
            }
            else -> null
        } ?: "${code.function}(${argumentsAssembly(cache, options)})"
    }

    private fun argumentsAssembly(cache: Cache, options: org.elixir_lang.beam.chunk.Code.Options): String =
            argumentsAssembly(code.arguments.zip(termList), cache, options)

    private fun argumentsAssembly(
            argumentTermPairList: List<Pair<Argument, Term>>,
            cache: Cache,
            options: org.elixir_lang.beam.chunk.Code.Options
    ): String =
            argumentTermPairList.joinToString(", ") { (argument, term) ->
                argument.assembly(term, cache, options)
            }

    private fun labelReference(term: Term, cache: Cache): String? =
        (term as? Label)?.let { label ->
            val code = cache.code!!

            code.labelIndexToFuncInfoIndex(label.index)?.let { funcInfoIndex ->
                val funcInfo = code[funcInfoIndex]
                val termList = funcInfo.termList
                val module = (termList[0] as? Atom)?.index?.let { index ->
                    if (index == 0) {
                        "nil"
                    } else {
                        cache.atoms?.getOrNull(index)?.string
                    }
                }
                val function = (termList[1] as? Atom)?.index?.let { index ->
                    if (index == 0) {
                        "nil"
                    } else {
                        cache.atoms?.getOrNull(index)?.string
                    }
                }
                val arity = (termList[2] as? Literal)?.index

                if (module != null && function != null && arity != null) {
                    reference(module, function, arity.toLong())
                } else {
                    null
                }
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
