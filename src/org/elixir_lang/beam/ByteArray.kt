package org.elixir_lang.beam

class ByteSubarray(private val byteArray: ByteArray, private val start: Int, val size: Int) {
    fun toUnsignedInt(): Long = shiftIn(0, 0)

    fun toLong(): Long {
        val firstUnsignedByte = getUnsignedByte(0)
        val signBit = firstUnsignedByte.ushr(7).and(0b1) == 1

        var long: Long = if (signBit) {
            0xFF.shl(24).or(
                    0xFF.shl(16)).or(
                    0xFF.shl(8)).or(
                    firstUnsignedByte).toLong()
        } else {
            firstUnsignedByte.toLong()
        }

        long = shiftIn(long, 1)

        return long
    }

    private fun shiftIn(initialValue: Long, startIndex: Int): Long {
        var acc = initialValue

        for (index in startIndex until size) {
            acc = acc.shl(8).or(getUnsignedByte(index).toLong())
        }

        return acc
    }

    private fun getUnsignedByte(index: Int) =
        get(index).toInt().and(0b1111_1111)

    operator fun get(index: Int) =
        if (index in 0 until size) {
            byteArray[start + index]
        } else {
            throw ArrayIndexOutOfBoundsException(index)
        }
}
