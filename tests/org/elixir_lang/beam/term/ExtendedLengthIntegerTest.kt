package org.elixir_lang.beam.term

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Regression tests for https://github.com/KronicDeth/intellij-elixir/issues/1052,
 * "ByteSubarray.get ArrayIndexOutOfBoundsException for 0".
 *
 * A compact-term integer whose tag has bits 7-5 set to `0b111` does not carry its byte length in
 * those bits - they are an escape. The length follows as its own compact term, and the real length
 * is that value plus 9, the next length the direct `bits7to5 + 2` form cannot express. See
 * `decode_int_length/2` in OTP's `lib/compiler/src/beam_disasm.erl`, which imitates
 * `get_erlang_integer()` in `beam_load.c`.
 *
 * The plugin read the byte after the tag as the length directly, with neither the nested decode nor
 * the `+ 9`. On the reported input that byte is a nested tag of `0x00`, which was taken as a length
 * of zero, and `Integer.from`'s unconditional `toLong()` then read byte 0 of an empty subarray.
 *
 * `0b1111_1001` is the tag under test: `001` selects Integer, bit 3 set leaves the 4-bit inline
 * form, bit 4 set leaves the 11-bit form, and bits 7-5 `111` selects the escape.
 */
class ExtendedLengthIntegerTest {
    private val extendedLengthIntegerTag = 0b1111_1001.toByte()

    /** A nested length term of `0x00` is `{u, 0}`, so the integer is 9 bytes, not 0. */
    @Test
    fun nestedLengthOfZeroMeansNineBytes() {
        val data = byteArrayOf(extendedLengthIntegerTag, 0x00) +
                byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 42)

        val (term, byteCount) = Term.from(data, 0, true)

        assertEquals(42L, (term as Integer).long)
        assertEquals("tag + nested length + 9 value bytes", 11, byteCount)
    }

    /** `{u, 1}` is 10 bytes. The nested term is a plain 4-bit inline value, so still one byte. */
    @Test
    fun nestedLengthOfOneMeansTenBytes() {
        val data = byteArrayOf(extendedLengthIntegerTag, 0b0001_0000.toByte()) +
                byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 7)

        val (term, byteCount) = Term.from(data, 0, true)

        assertEquals(7L, (term as Integer).long)
        assertEquals(12, byteCount)
    }

    /**
     * The direct form, unchanged by the fix and already correct: `bits7to5` of 0 through 6 mean
     * byte lengths 2 through 8.
     */
    @Test
    fun directLengthsAreBitsSevenToFivePlusTwo() {
        for (bits7to5 in 0..6) {
            val tag = ((bits7to5 shl 5) or 0b1_1001).toByte()
            val expectedLength = bits7to5 + 2
            val data = ByteArray(1 + expectedLength).also {
                it[0] = tag
                it[expectedLength] = 5
            }

            val (term, byteCount) = Term.from(data, 0, true)

            assertEquals("bits7to5=$bits7to5", 5L, (term as Integer).long)
            assertEquals("bits7to5=$bits7to5", 1 + expectedLength, byteCount)
        }
    }
}
