package org.elixir_lang.beam.chunk

import com.intellij.openapi.util.component1
import com.intellij.openapi.util.component2
import org.elixir_lang.beam.chunk.Chunk.unsignedInt
import org.elixir_lang.beam.chunk.imports.Import

class Imports(private val importList: List<Import>) {
    fun size() = importList.size
    operator fun get(index: Int): Import? = importList[index]

    companion object {
        fun from(chunk: Chunk, atoms: Atoms?): Imports? =
                if (chunk.typeID == Chunk.TypeID.IMPT.toString() && chunk.data.size >= 4) {
                    var offset = 0
                    val (importCount, importCountByteCount) = unsignedInt(chunk.data, offset)
                    offset += importCountByteCount

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
