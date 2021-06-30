package org.elixir_lang.beam.chunk

import com.intellij.openapi.util.Pair
import okhttp3.internal.and
import org.jetbrains.annotations.Contract
import java.io.DataInputStream
import java.io.IOException

/**
 * Chunk of a `.beam` file.  Same chunk format as base IFF
 */
class Chunk private constructor(@JvmField val typeID: String, @JvmField val data: ByteArray) {
    enum class TypeID(private val typeID: String) {
        ATOM("Atom"),
        ATTR("Attr"),
        ATU8("AtU8"),
        CINF("CInf"),
        CODE("Code"),
        DOCS("Docs"),
        DBGI("Dbgi"),
        EXDC("ExDc"),
        EXPT("ExpT"),
        FUNT("FunT"),
        IMPT("ImpT"),
        LINE("Line"),
        LITT("LitT"),
        LOCT("LocT"),
        STRT("StrT");

        override fun toString(): String {
            return typeID
        }
    }

    companion object {
        private const val ALIGNMENT = 4
        private const val BYTE_BIT_COUNT = 8
        private const val UNSIGNED_INT_BYTE_COUNT = 4
        private const val UNSIGNED_SHORT_BYTE_COUNT = 2

        fun from(dataInputStream: DataInputStream): Chunk? =
                typeID(dataInputStream)?.let { typeID ->
                    length(dataInputStream)?.let { length ->
                        dataInputStream.safeReadBytes(length.toInt())?.let { data ->
                            val padding = ((ALIGNMENT - length % ALIGNMENT) % ALIGNMENT).toInt()
                            dataInputStream.skipBytes(padding)
                            Chunk(typeID, data)
                        }
                    }
                }

        fun length(dataInputStream: DataInputStream): Long? = readUnsignedInt(dataInputStream)

        private fun readUnsignedInt(dataInputStream: DataInputStream): Long? =
            dataInputStream.safeReadBytes(32 / 8)?.let { bytes ->
                unsignedInt(bytes).first
            }

        fun typeID(dataInputStream: DataInputStream): String? =
                dataInputStream.safeReadBytes(4)?.let { bytes ->
                    String(bytes)
                }

        @Contract(pure = true)
        fun unsignedByte(signedByte: Byte): Pair<Int, Int> = Pair.pair(signedByte and 0xFF, 1)

        @Contract(pure = true)
        fun unsignedInt(bytes: ByteArray): Pair<Long, Int> = unsignedInt(bytes, 0)

        @JvmStatic
        @Contract(pure = true)
        fun unsignedInt(bytes: ByteArray, offset: Int): Pair<Long, Int> {
            assert(bytes.size >= offset + UNSIGNED_INT_BYTE_COUNT)
            var unsignedInt: Long = 0
            for (i in 0 until UNSIGNED_INT_BYTE_COUNT) {
                val unsignedByte = unsignedByte(bytes[offset + i]).first
                unsignedInt += (unsignedByte shl BYTE_BIT_COUNT * (UNSIGNED_INT_BYTE_COUNT - 1 - i)).toLong()
            }
            return Pair.pair(unsignedInt, UNSIGNED_INT_BYTE_COUNT)
        }

        fun unsignedShort(bytes: ByteArray, offset: Int): Pair<Int, Int> {
            assert(bytes.size >= offset + UNSIGNED_SHORT_BYTE_COUNT)
            var unsignedShort = 0
            for (i in 0 until UNSIGNED_SHORT_BYTE_COUNT) {
                val unsignedByte = unsignedByte(bytes[offset + i]).first
                unsignedShort += unsignedByte shl BYTE_BIT_COUNT * (UNSIGNED_SHORT_BYTE_COUNT - 1 - i)
            }
            return Pair.pair(unsignedShort, UNSIGNED_SHORT_BYTE_COUNT)
        }
    }
}

fun DataInputStream.safeReadBytes(count: Int): ByteArray? {
    val bytes = ByteArray(count)

    return try {
        readFully(bytes)

        bytes
    } catch (ioException: IOException) {
        null
    }
}
