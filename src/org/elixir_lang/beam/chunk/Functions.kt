package org.elixir_lang.beam.chunk

import com.intellij.openapi.util.component1
import com.intellij.openapi.util.component2
import org.elixir_lang.beam.chunk.Chunk.unsignedInt
import org.elixir_lang.beam.chunk.functions.Function

class Functions(private val functionList: List<Function>) {
    operator fun get(index: Int): Function = functionList[index]
    fun getOrNull(index: Int): Function? = functionList.getOrNull(index)
    fun size(): Int = functionList.size

    companion object {
        fun from(chunk: Chunk, atoms: Atoms?): Functions? {
            val data = chunk.data
            var offset = 0

            val (size, sizeByteCount) = unsignedInt(data, offset)
            offset += sizeByteCount

            val functionList = mutableListOf<Function>()

            repeat(size.toInt()) {
                val (function, functionByteCount) = Function.from(data, offset, atoms)
                offset += functionByteCount

                functionList.add(function)
            }

            return Functions(functionList)
        }
    }
}
