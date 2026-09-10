package org.elixir_lang.beam

import com.ericsson.otp.erlang.OtpErlangDecodeException
import com.ericsson.otp.erlang.OtpErlangFun
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpExternal
import com.ericsson.otp.erlang.OtpInputStream
import org.elixir_lang.beam.term.ByteCount
import java.io.IOException
import java.util.zip.Inflater
import java.util.zip.InflaterInputStream

/** DEFLATE cannot inflate past 1032:1, so a larger declared size is corrupt. */
internal const val DEFLATE_MAX_RATIO = 1032L

/** Above the largest compressed term in real modules: Elixir 1.11's `String.Unicode` debug info, at 18 MB. */
internal const val MAX_INFLATED_TERM_BYTES = 32L * 1024 * 1024

fun binaryToTerm(byteArray: ByteArray, offset: Int): Pair<OtpErlangObject, ByteCount> {
    val stream = BoundedOtpInputStream(byteArray, offset, byteArray.size, compressible = true)
    val term = stream.read_any()

    return Pair(term, stream.pos - offset)
}

/**
 * JInterface allocates the array a length or arity field describes before it reads the contents, so a corrupt
 * field is an `OutOfMemoryError`. Every element at every depth takes at least one byte, so every length a term
 * declares is spent from one budget of its bytes.
 */
private class BoundedOtpInputStream(buf: ByteArray, offset: Int, length: Int, private val compressible: Boolean) :
    OtpInputStream(buf, offset, length, 0) {
    private var budget: Long = available().toLong()
    private var depth = 0

    override fun read_any(): OtpErlangObject {
        depth++

        try {
            return super.read_any()
        } finally {
            depth--
        }
    }

    override fun read_binary(): ByteArray {
        spend(peek { read1skip_version(); read4BE() }, "binary")
        return super.read_binary()
    }

    override fun read_bitstr(padBits: IntArray): ByteArray {
        spend(peek { read1skip_version(); read4BE() }, "bitstring")
        return super.read_bitstr(padBits)
    }

    override fun read_integer_byte_array(): ByteArray {
        peek { if (read1skip_version() == OtpExternal.largeBigTag) read4BE() else null }
            ?.let { spend(it, "integer") }
        return super.read_integer_byte_array()
    }

    override fun read_list_head(): Int = super.read_list_head().also { spend(it, "list") }

    override fun read_tuple_head(): Int = super.read_tuple_head().also { spend(it, "tuple") }

    override fun read_fun(): OtpErlangFun {
        peek {
            when (read1skip_version()) {
                OtpExternal.funTag -> read4BE()
                OtpExternal.newFunTag -> {
                    skip(NEW_FUN_BYTES_BEFORE_FREE_VARIABLE_COUNT)
                    read4BE()
                }
                else -> null
            }
        }?.let { spend(it, "fun") }
        return super.read_fun()
    }

    /**
     * Replaced rather than guarded: JInterface allocates the declared size before inflating, and decodes the
     * result with an unbounded stream of its own.
     */
    override fun read_compressed(): OtpErlangObject {
        if (peek { read1skip_version() } != OtpExternal.compressedTag) return super.read_compressed()

        // As in the VM: each nested level would multiply DEFLATE's ratio.
        if (!compressible || depth > 1) {
            throw OtpErlangDecodeException("compressed term below the top level")
        }

        read1skip_version()
        val size = read4BE()
        val inflatable = minOf(available() * DEFLATE_MAX_RATIO, MAX_INFLATED_TERM_BYTES)

        if (size !in 0..inflatable) {
            throw OtpErlangDecodeException(
                "compressed term declares ${Integer.toUnsignedLong(size)}, beyond the $inflatable that ${available()} bytes may inflate to"
            )
        }

        val inflater = Inflater()
        val inflated = try {
            val inflating = InflaterInputStream(this, inflater, INFLATE_BUFFER_BYTES)
            // Grows with what actually inflates. As in the VM, the data must end exactly at the declared size.
            val bytes = inflating.readNBytes(size)

            if (bytes.size != size || inflating.read() != -1) {
                throw OtpErlangDecodeException("compressed term does not inflate to the $size bytes it declares")
            }

            bytes
        } catch (_: IOException) {
            throw OtpErlangDecodeException("Cannot read from input stream")
        } finally {
            // The inflater reads ahead; giving back what it did not use keeps the term's byte count exact.
            setPos(getPos() - inflater.remaining)
            inflater.end()
        }

        return BoundedOtpInputStream(inflated, 0, size, compressible = false).read_any()
    }

    private inline fun <T> peek(read: () -> T): T {
        val start = getPos()

        try {
            return read()
        } finally {
            setPos(start)
        }
    }

    private fun spend(declared: Int, what: String) {
        // read4BE returns a field of 2^31 or more as a negative Int.
        val unsigned = Integer.toUnsignedLong(declared)

        if (unsigned > budget) {
            throw OtpErlangDecodeException("$what declares $unsigned, beyond the $budget bytes this term has left")
        }

        budget -= unsigned
    }

    private companion object {
        const val INFLATE_BUFFER_BYTES = 8192

        /** Size, arity, MD5 and index. */
        const val NEW_FUN_BYTES_BEFORE_FREE_VARIABLE_COUNT = 4L + 1 + 16 + 4
    }
}
