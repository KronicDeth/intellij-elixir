package org.elixir_lang.beam

import com.ericsson.otp.erlang.OtpErlangBinary
import com.ericsson.otp.erlang.OtpErlangDecodeException
import org.elixir_lang.beam.BeamBytes.bytesOf
import org.elixir_lang.beam.BeamBytes.unsignedInt
import org.elixir_lang.beam.BeamBytes.writeUnsignedInt
import org.junit.Assert
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.util.zip.Deflater

/**
 * JInterface allocates an array sized by a term's length or arity field before reading what it describes,
 * so a corrupt field is an `OutOfMemoryError` rather than a decode failure. Every case declares an array the
 * JVM refuses whatever the heap, so an unbounded decode fails on any machine.
 */
class BinaryToTermTest {
    @Test
    fun aBinaryLongerThanTheBytesLeftIsRefused() = assertRefused(BINARY, unsignedInt(Int.MAX_VALUE))

    @Test
    fun aBitstringLongerThanTheBytesLeftIsRefused() = assertRefused(BITSTRING, unsignedInt(Int.MAX_VALUE), 0)

    @Test
    fun aListLongerThanTheBytesLeftIsRefused() = assertRefused(LIST, unsignedInt(Int.MAX_VALUE))

    @Test
    fun aTupleLongerThanTheBytesLeftIsRefused() = assertRefused(LARGE_TUPLE, unsignedInt(Int.MAX_VALUE))

    /** JInterface rejects an arity of `Int.MAX_VALUE` itself, because it allocates one byte more. */
    @Test
    fun aBigIntegerLongerThanTheBytesLeftIsRefused() = assertRefused(LARGE_BIG, unsignedInt(Int.MAX_VALUE - 1), 0)

    /** The free variables are allocated after the pid, module, index and uniq, so those have to decode. */
    @Test
    fun aFunWithMoreFreeVariablesThanBytesLeftIsRefused() =
        assertRefused(FUN, unsignedInt(Int.MAX_VALUE), *PID, *MODULE, *SMALL_ZERO, *SMALL_ZERO)

    @Test
    fun aNewFunWithMoreFreeVariablesThanBytesLeftIsRefused() =
        assertRefused(
            NEW_FUN, unsignedInt(0), 0, ByteArray(16), unsignedInt(0), unsignedInt(Int.MAX_VALUE),
            *MODULE, *SMALL_ZERO, *SMALL_ZERO, *PID,
        )

    @Test
    fun aCompressedTermLargerThanDeflateCouldProduceIsRefused() =
        assertRefused(COMPRESSED, unsignedInt(Int.MAX_VALUE), deflate(bytesOf(NIL)))

    /** JInterface decodes the inflated bytes with a fresh stream of its own. */
    @Test
    fun aTermInsideACompressedTermIsBoundedToo() {
        val inner = bytesOf(BINARY, unsignedInt(Int.MAX_VALUE))
        assertRefused(COMPRESSED, unsignedInt(inner.size), deflate(inner))
    }

    /** DEFLATE's ratio alone would allow this from about 32 KB. */
    @Test
    fun aCompressedTermBeyondTheCapIsRefused() {
        val inner = binaryTermOf(byteCount = (32 shl 20) + 1)

        assertRefused(COMPRESSED, unsignedInt(inner.size), deflate(inner))
    }

    /** The size of Elixir 1.11's `String.Unicode` debug info, the largest compressed term measured in real modules. */
    @Test
    fun theLargestRealCompressedTermSizeStillDecodes() {
        val inner = binaryTermOf(byteCount = 17_960_030)

        val (term, _) = binaryToTerm(bytesOf(VERSION, COMPRESSED, unsignedInt(inner.size), deflate(inner)), 0)

        Assert.assertEquals(inner.size - 5, (term as OtpErlangBinary).size())
    }

    /** The VM accepts a compressed term only straight after the version byte, so nesting cannot multiply DEFLATE's ratio. */
    @Test
    fun aCompressedTermInsideACompressedTermIsRefused() {
        val inner = bytesOf(COMPRESSED, unsignedInt(1), deflate(bytesOf(NIL)))

        assertNotDecoded(COMPRESSED, unsignedInt(inner.size), deflate(inner))
    }

    @Test
    fun aCompressedTermInsideAListIsRefused() =
        assertNotDecoded(LIST, unsignedInt(1), COMPRESSED, unsignedInt(1), deflate(bytesOf(NIL)), NIL)

    /** Enough bytes left that DEFLATE's ratio allows the declared size, so only not allocating it up front helps. */
    @Test
    fun aCompressedTermIsNotAllocatedAtItsDeclaredSize() {
        val failed = try {
            binaryToTerm(bytesOf(VERSION, COMPRESSED, unsignedInt(Int.MAX_VALUE), ByteArray(2_100_000)), 0)
            null
        } catch (caught: OtpErlangDecodeException) {
            caught
        }

        Assert.assertNotNull("corrupt compressed data must fail to decode, not to allocate", failed)
    }

    /** Each head fits the bytes left, but every element at every depth takes a byte, so together they cannot. */
    @Test
    fun nestedListsLongerTogetherThanTheTermAreRefused() {
        val heads = ByteArrayOutputStream().apply { repeat(1000) { write(bytesOf(LIST, unsignedInt(10_000))) } }

        assertRefused(heads.toByteArray(), ByteArray(10_000))
    }

    /** Otherwise the first term of the inflated data decodes as though it were all of it. */
    @Test
    fun aCompressedTermThatInflatesPastItsDeclaredSizeIsRefused() {
        val refused = try {
            binaryToTerm(bytesOf(VERSION, COMPRESSED, unsignedInt(1), deflate(bytesOf(NIL, NIL))), 0)
            null
        } catch (caught: OtpErlangDecodeException) {
            caught
        }

        Assert.assertNotNull("compressed data that inflates past its declared size must fail to decode", refused)
    }

    /** The inflater reads ahead of the compressed bytes, into whatever follows the term. */
    @Test
    fun aCompressedTermCountsOnlyTheBytesItOccupies() {
        val inner = bytesOf(BINARY, unsignedInt(3), 'a', 'b', 'c')
        val term = bytesOf(VERSION, COMPRESSED, unsignedInt(inner.size), deflate(inner))

        Assert.assertEquals(term.size, binaryToTerm(term + ByteArray(16), 0).second)
    }

    /** A megabyte of zeros deflates at close to DEFLATE's limit, so a bound tighter than that rejects it. */
    @Test
    fun aHighlyCompressedTermStillDecodes() {
        val binary = ByteArray(1 shl 20)
        val inner = bytesOf(BINARY, unsignedInt(binary.size), binary)

        val (term, _) = binaryToTerm(bytesOf(VERSION, COMPRESSED, unsignedInt(inner.size), deflate(inner)), 0)

        Assert.assertEquals(binary.size, (term as OtpErlangBinary).size())
    }

    private fun assertRefused(vararg term: Any) {
        val refused = try {
            binaryToTerm(bytesOf(VERSION, *term), 0)
            null
        } catch (caught: OtpErlangDecodeException) {
            caught
        }

        Assert.assertNotNull("a length the bytes cannot hold must be refused before it is allocated", refused)
        Assert.assertTrue("the refusal must say so, got ${refused!!.message}", refused.message.orEmpty().contains("beyond"))
    }

    /** A binary term of zeros, [byteCount] bytes long with its tag and length. */
    private fun binaryTermOf(byteCount: Int): ByteArray =
        ByteArray(byteCount).also {
            it[0] = BINARY.toByte()
            writeUnsignedInt(it, 1, byteCount - 5)
        }

    private fun assertNotDecoded(vararg term: Any) {
        val failed = try {
            binaryToTerm(bytesOf(VERSION, *term), 0)
            null
        } catch (caught: OtpErlangDecodeException) {
            caught
        }

        Assert.assertNotNull("a compressed term below the top level must fail to decode", failed)
    }

    private fun deflate(bytes: ByteArray): ByteArray {
        val deflater = Deflater().apply {
            setInput(bytes)
            finish()
        }
        val output = ByteArrayOutputStream()
        val buffer = ByteArray(8192)

        while (!deflater.finished()) {
            output.write(buffer, 0, deflater.deflate(buffer))
        }
        deflater.end()

        return output.toByteArray()
    }

    private companion object {
        const val VERSION = 131
        const val COMPRESSED = 80
        const val BITSTRING = 77
        const val SMALL_INTEGER = 97
        const val LARGE_TUPLE = 105
        const val NIL = 106
        const val LIST = 108
        const val BINARY = 109
        const val LARGE_BIG = 111
        const val NEW_FUN = 112
        const val FUN = 117
        const val PID_TAG = 103
        const val SMALL_ATOM_UTF8 = 119

        val SMALL_ZERO = arrayOf<Any>(SMALL_INTEGER, 0)
        val MODULE = arrayOf<Any>(SMALL_ATOM_UTF8, 1, 'm')
        val PID = arrayOf<Any>(PID_TAG, SMALL_ATOM_UTF8, 1, 'n', unsignedInt(0), unsignedInt(0), 0)
    }
}
