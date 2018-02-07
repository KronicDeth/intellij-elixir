package org.elixir_lang.beam.chunk.call_definitions

import com.intellij.openapi.util.component1
import com.intellij.openapi.util.component2
import org.elixir_lang.beam.chunk.Atoms
import org.elixir_lang.beam.chunk.Chunk
import org.elixir_lang.beam.chunk.Chunk.unsignedInt
import org.elixir_lang.beam.term.unsignedIntToInt

// atomIndex is 1-based
class CallDefinition(val atomIndex: Long, val arity: Long, val label: Long, atoms: Atoms?): Comparable<CallDefinition> {
    val name: String? = atoms?.getOrNull(unsignedIntToInt(atomIndex))?.string

    override fun compareTo(other: CallDefinition): Int {
        val nameAtomIndexComparison = if (name != null && other.name != null) {
            name.compareTo(other.name)
        } else {
            atomIndex.compareTo(other.atomIndex)
        }

        return if (nameAtomIndexComparison != 0) {
            nameAtomIndexComparison
        } else {
            arity.compareTo(other.arity)
        }
    }

    companion object {
        fun from(chunk: Chunk, offset: Int, atoms: Atoms?): Pair<CallDefinition, Int> {
            var internalOffset = offset

            val (atomIndex, atomIndexByteCount) = unsignedInt(chunk.data, internalOffset)
            internalOffset += atomIndexByteCount

            val (arity, arityByteCount) = unsignedInt(chunk.data, internalOffset)
            internalOffset += arityByteCount

            val (label, labelByteCount) = unsignedInt(chunk.data, internalOffset)
            internalOffset += labelByteCount

            return Pair(CallDefinition(atomIndex, arity, label, atoms), internalOffset - offset)
        }
    }
}
