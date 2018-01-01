package org.elixir_lang.beam.chunk.imports

import com.intellij.openapi.util.component1
import com.intellij.openapi.util.component2
import org.elixir_lang.beam.chunk.Atoms
import org.elixir_lang.beam.chunk.Chunk
import org.elixir_lang.beam.chunk.Chunk.unsignedInt

data class Import(val moduleAtomIndex: Long, val functionAtomIndex: Long, val arity: Long, private val atoms: Atoms?) {
    val functionName: String?
        get() = atoms?.get(functionAtomIndex.toInt())?.string

    val moduleName: String?
        get() = atoms?.get(moduleAtomIndex.toInt())?.string

    companion object {
        fun from(chunk: Chunk, offset: Int, atoms: Atoms?): Pair<Import, Int> {
            var internalOffset = offset

            val (moduleAtomIndex, moduleAtomIndexByteCount) = unsignedInt(chunk.data, internalOffset)
            internalOffset += moduleAtomIndexByteCount

            val (functionAtomIndex, functionAtomIndexByteCount) = unsignedInt(chunk.data, internalOffset)
            internalOffset += functionAtomIndexByteCount

            val (arity, arityByteCount) = unsignedInt(chunk.data, internalOffset)
            internalOffset += arityByteCount

            return Pair(Import(moduleAtomIndex, functionAtomIndex, arity, atoms), internalOffset - offset)
        }
    }
}
