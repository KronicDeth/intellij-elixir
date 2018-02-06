package org.elixir_lang.beam.chunk.functions

import com.intellij.openapi.util.component1
import com.intellij.openapi.util.component2
import org.elixir_lang.beam.chunk.Atoms
import org.elixir_lang.beam.chunk.Chunk.unsignedInt
import org.elixir_lang.beam.term.ByteCount

class Function(
        val atomIndex: Long,
        val name: String?,
        val arity: Long,
        val codeOffset: Long,
        val index: Long,
        val freeVariableCount: Long,
        val oUnique: Long
) {
    companion object {
        fun from(data: ByteArray, offset: Int, atoms: Atoms?): Pair<Function, ByteCount> {
            var internalOffset = offset

            val (atomIndex, atomIndexByteCount) = unsignedInt(data, internalOffset)
            internalOffset += atomIndexByteCount

            val name = atoms?.getOrNull(atomIndex.toInt())?.string

            val (arity, arityByteCount) = unsignedInt(data, internalOffset)
            internalOffset += arityByteCount

            val (codeOffset, codeOffsetByteCount) = unsignedInt(data, internalOffset)
            internalOffset += codeOffsetByteCount

            val (index, indexByteCount) = unsignedInt(data, internalOffset)
            internalOffset += indexByteCount

            val (freeVariableCount, freeVariableCountByteCount) = unsignedInt(data, internalOffset)
            internalOffset += freeVariableCountByteCount

            // TODO figure out what "o" in ouniq stands for
            val (oUnique, oUniqueByteCount) = unsignedInt(data, internalOffset)
            internalOffset += oUniqueByteCount

            return Pair(
                    Function(atomIndex, name, arity, codeOffset, index, freeVariableCount, oUnique),
                    internalOffset - offset
            )
        }
    }
}
