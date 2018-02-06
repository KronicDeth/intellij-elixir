package org.elixir_lang.beam.chunk

import org.elixir_lang.beam.chunk.Chunk.unsignedByte
import org.elixir_lang.beam.chunk.Chunk.unsignedInt
import org.elixir_lang.beam.chunk.atoms.Atom
import java.nio.charset.Charset
import java.util.*

class Atoms private constructor(private val atomList: List<Atom>) {

    /**
     * @param index 1-based index.  1 is reserved for {#link moduleName}
     */
    operator fun get(index: Int): Atom = atomList[index - 1]

    /**
     * @param index 1-based index.  1 is reserved for {#link moduleName}
     */
    fun getOrNull(index: Int): Atom? = atomList.getOrNull(index - 1)
    fun moduleName(): String? = atomList.getOrNull(0)?.string
    fun size(): Int = atomList.size

    companion object {
        fun from(chunk: Chunk, typeID: Chunk.TypeID, charset: Charset): Atoms? {
            var atoms: Atoms? = null

            if (chunk.typeID == typeID.toString() && chunk.data.size >= 4) {
                var offset = 0
                val atomCountByteCount = unsignedInt(chunk.data, offset)
                val atomCount = atomCountByteCount.first
                offset += atomCountByteCount.second

                val atomList = ArrayList<Atom>()

                for (i in 1..atomCount) {
                    val atomLengthByteCount = unsignedByte(chunk.data[offset])
                    val atomLength = atomLengthByteCount.first
                    offset += atomLengthByteCount.second

                    val string = String(chunk.data, offset, atomLength, charset)
                    offset += atomLength
                    atomList.add(Atom(i.toInt(), atomLength, string))
                }

                atoms = Atoms(atomList)
            }

            return atoms
        }
    }
}
