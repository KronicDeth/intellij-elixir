package org.elixir_lang.beam.chunk.call_definitions

import com.intellij.openapi.util.component1
import com.intellij.openapi.util.component2
import org.elixir_lang.beam.RefusedBeamData
import org.elixir_lang.beam.chunk.Atoms
import org.elixir_lang.beam.chunk.Chunk
import org.elixir_lang.beam.chunk.Chunk.Companion.unsignedInt

// atomIndex is 1-based
class CallDefinition(val atomIndex: Long, val arity: Long, val label: Long, atoms: Atoms?): Comparable<CallDefinition> {
    val name: String? = atoms?.getOrNull(atomIndex)?.string

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
        /** `MAX_ARG` in OTP's `erl_vm.h`. The decompiler builds one parameter name per unit of arity. */
        private const val MAX_ARITY = 255L

        fun from(chunk: Chunk, offset: Int, atoms: Atoms?): Pair<CallDefinition, Int> {
            var internalOffset = offset

            val (atomIndex, atomIndexByteCount) = unsignedInt(chunk.data, internalOffset)
            internalOffset += atomIndexByteCount

            val (arity, arityByteCount) = unsignedInt(chunk.data, internalOffset)
            internalOffset += arityByteCount

            if (arity > MAX_ARITY) {
                throw RefusedBeamData("${chunk.typeID} declares arity $arity, beyond the $MAX_ARITY the VM allows")
            }

            val (label, labelByteCount) = unsignedInt(chunk.data, internalOffset)
            internalOffset += labelByteCount

            return Pair(CallDefinition(atomIndex, arity, label, atoms), internalOffset - offset)
        }
    }
}
