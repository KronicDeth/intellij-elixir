package org.elixir_lang.beam.chunk

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.component1
import com.intellij.openapi.util.component2
import org.elixir_lang.beam.Cache
import org.elixir_lang.beam.chunk.Chunk.unsignedInt
import org.elixir_lang.beam.chunk.code.Operation

class Code(private val operationList: List<Operation>) {
    data class Options(val inline: Inline = Inline(), val showArgumentNames: Boolean = true) {
        data class Inline(
                val atoms: Boolean = true,
                val functions: Boolean = false,
                val imports: Boolean = false,
                val integers: Boolean = true,
                val labels: Boolean = true,
                val literals: Boolean = false
        ) {
            companion object {
                val UNAMBIGUOUS = Inline(
                        atoms = true,
                        functions = false,
                        imports = false,
                        integers = true,
                        literals = false
                )
            }
        }

        companion object {
            val UNAMBIGUOUS = Options(Inline.Companion.UNAMBIGUOUS)
        }
    }

    fun assembly(cache: Cache, options: Options): String =
        operationList.joinToString("\n") { operation ->
            val operationAssembly = operation.assembly(cache, options)
            val function = operation.code.function

            val indent = when (function) {
                "label" -> ""
                "line" -> "  "
                "func_info" -> "    "
                else -> "      "
            }
            val suffix = when (function) {
                "badmatch", "call_ext_last", "call_ext_only", "call_last", "return" -> "\n"
                else -> ""
            }

            "$indent$operationAssembly$suffix"
        }

    operator fun get(index: Int): Operation = operationList[index]
    fun size(): Int = operationList.size

    companion object {
        private val LOGGER = Logger.getInstance(Code::class.java)

        fun from(chunk: Chunk, literalFloat: Boolean = true): Code {
            val data = chunk.data
            var offset = 0

            val (_unknown, unknownByteCount) = unsignedInt(data, offset)
            offset += unknownByteCount

            val (version, versionByteCount) = unsignedInt(data, offset)
            offset += versionByteCount

            if (version != 0L) {
                LOGGER.error(
                        "Code version ($version) differs from expect 0.  There was an incompatible change in " +
                                "https://github.com/erlang/otp/blob/master/lib/compiler/src/genop.tab."
                )
            }

            val (maxOpcode, maxOpcodeByteCount) = unsignedInt(data, offset)
            offset += maxOpcodeByteCount

            val expectedMaxOpcode = org.elixir_lang.beam.chunk.code.operation.Code.values().max()?.ordinal ?: 0
            if (maxOpcode > expectedMaxOpcode) {
                LOGGER.error(
                        "Max opcode ($maxOpcode) exceeds expected max opcode ($expectedMaxOpcode).  Additional " +
                                "opcodes have been added to the end of " +
                                "https://github.com/erlang/otp/blob/master/lib/compiler/src/genop.tab."
                )
            }

            val (labelCount, labelCountByteCount) = unsignedInt(data, offset)
            offset += labelCountByteCount

            val (functionCount, functionCountByteCount) = unsignedInt(data, offset)
            offset += functionCountByteCount

            val operationList = mutableListOf<Operation>()

            while (offset < data.lastIndex) {
                val (operation, operationByteCount) = Operation.from(data, offset, literalFloat)

                operationList.add(operation)
                offset += operationByteCount
            }

            return Code(operationList)
        }
    }
}
