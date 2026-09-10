package org.elixir_lang.beam.chunk

import org.elixir_lang.beam.declaredCount
import com.intellij.openapi.util.component1
import com.intellij.openapi.util.component2
import org.elixir_lang.beam.chunk.Chunk.Companion.unsignedInt
import org.elixir_lang.beam.chunk.imports.Import

class Imports(private val importList: List<Import>) {
    operator fun get(index: Int): Import = importList[index]
    fun getOrNull(index: Int): Import? = importList.getOrNull(index)
    fun size() = importList.size

    companion object {
        /** Module atom index, function atom index and arity, each an unsigned int. */
        private const val IMPORT_BYTE_COUNT = 3 * Int.SIZE_BYTES

        fun from(chunk: Chunk, atoms: Atoms?): Imports? =
                if (chunk.typeID == Chunk.TypeID.IMPT.toString() && chunk.data.size >= 4) {
                    var offset = 0
                    val (declaredImportCount, importCountByteCount) = unsignedInt(chunk.data, offset)
                    offset += importCountByteCount
                    val importCount = declaredCount(
                        declaredImportCount,
                        chunk.data.size - offset,
                        IMPORT_BYTE_COUNT,
                        "ImpT",
                    )

                    val importList: MutableList<Import> = arrayListOf()

                    for (i in 0 until importCount) {
                        val (import, importByteCount) = Import.from(chunk, offset, atoms)

                        importList.add(import)
                        offset += importByteCount
                    }

                    Imports(importList)
                } else {
                    null
                }
    }
}
