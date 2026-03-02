package org.elixir_lang.beam.chunk

import org.elixir_lang.beam.chunk.Chunk.Companion.unsignedByte
import org.elixir_lang.beam.chunk.Chunk.Companion.unsignedInt
import org.elixir_lang.beam.chunk.atoms.Atom
import java.nio.charset.Charset

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
        private const val TAG_U = 0

        fun from(chunk: Chunk, typeID: Chunk.TypeID, charset: Charset): Atoms? {
            var atoms: Atoms? = null

            if (chunk.typeID == typeID.toString() && chunk.data.size >= 4) {
                var offset = 0
                val atomCountByteCount = unsignedInt(chunk.data, offset)
                val atomCountSigned = atomCountByteCount.first.toInt()
                val usesTaggedLengths = (typeID == Chunk.TypeID.ATU8 && atomCountSigned < 0)
                val atomCount = if (usesTaggedLengths) -atomCountSigned else atomCountByteCount.first.toInt()
                offset += atomCountByteCount.second

                val atomList = ArrayList<Atom>()

                for (i in 1..atomCount) {
                    val atomLengthByteCount = if (usesTaggedLengths) {
                        decodeTaggedUnsignedInt(chunk.data, offset)
                    } else {
                        val lengthByteCount = unsignedByte(chunk.data[offset])
                        Pair(lengthByteCount.first, lengthByteCount.second)
                    } ?: return null

                    val atomLength = atomLengthByteCount.first
                    offset += atomLengthByteCount.second

                    val string = String(chunk.data, offset, atomLength, charset)
                    offset += atomLength
                    atomList.add(Atom(i, atomLength, string))
                }

                atoms = Atoms(atomList)
            }

            return atoms
        }

        /**
         * Decode a BEAM tagged unsigned integer (tag_u) as encoded by beam_asm:encode(?tag_u, N).
         */
        private fun decodeTaggedUnsignedInt(data: ByteArray, offset: Int): Pair<Int, Int>? {
            if (offset >= data.size) return null

            val first = data[offset].toInt() and 0xFF
            val tag = first and 0x07
            if (tag != TAG_U) return null

            return when {
                // Small: 1-byte, tag in low 3 bits, bit 3 == 0.
                (first and 0x08) == 0 -> Pair(first ushr 4, 1)

                // 2-byte: bit 3 == 1, bit 4 == 0.
                (first and 0x18) == 0x08 -> {
                    if (offset + 1 >= data.size) return null
                    val second = data[offset + 1].toInt() and 0xFF
                    val value = ((first and 0xE0) shl 3) or second
                    Pair(value, 2)
                }

                // Multi-byte (2..8 bytes) or extended length.
                else -> {
                    if ((first and 0xF8) == 0xF8) {
                        val lengthResult = decodeTaggedUnsignedInt(data, offset + 1) ?: return null
                        val lengthByteCount = lengthResult.second
                        val byteCount = lengthResult.first + 9
                        val start = offset + 1 + lengthByteCount
                        if (start + byteCount > data.size) return null
                        val value = readUnsignedBigEndian(data, start, byteCount)
                        Pair(value, 1 + lengthByteCount + byteCount)
                    } else {
                        val byteCount = (first ushr 5) + 2
                        val start = offset + 1
                        if (start + byteCount > data.size) return null
                        val value = readUnsignedBigEndian(data, start, byteCount)
                        Pair(value, 1 + byteCount)
                    }
                }
            }
        }

        private fun readUnsignedBigEndian(data: ByteArray, offset: Int, length: Int): Int {
            var value = 0
            for (i in 0 until length) {
                value = (value shl 8) or (data[offset + i].toInt() and 0xFF)
            }
            return value
        }
    }
}
