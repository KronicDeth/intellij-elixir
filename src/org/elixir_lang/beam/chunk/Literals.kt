package org.elixir_lang.beam.chunk

import com.ericsson.otp.erlang.OtpErlangObject
import com.intellij.openapi.util.component1
import com.intellij.openapi.util.component2
import org.elixir_lang.beam.chunk.Chunk.Companion.unsignedInt
import org.elixir_lang.beam.chunk.literals.literal
import org.elixir_lang.beam.RefusedBeamData
import org.elixir_lang.beam.term.unsignedIntToInt
import java.util.zip.Inflater

class Literals(private val termList: List<OtpErlangObject>) {
    operator fun get(index: Int): OtpErlangObject = termList[index]
    fun getOrNull(index: Int): OtpErlangObject? = termList.getOrNull(index)
    fun size(): Int = termList.size

    companion object {
        /**
         * `inflatedSize` is an unsigned 32-bit field read straight out of the chunk, so a corrupt
         * one asks for up to 2 GiB from a file of a few kilobytes - and `OutOfMemoryError` is an
         * `Error`, which callers deliberately do not catch.
         */
        private const val MAX_INFLATED_SIZE = 10L * 1024 * 1024

        /** Throws on literals that do not decode: null would mean the module has none, which SDK detection caches. */
        fun from(chunk: Chunk): Literals {
            val data = chunk.data
            var offset = 0

            val (inflatedSize, inflatedSizeByteCount) = unsignedInt(data, offset)
            offset += inflatedSizeByteCount

            // OTP 28+ uses uncompressed literals and stores a zero size sentinel.
            // see https://github.com/erlang/otp/blob/f80e9c1c4a3f271e39fcdb0be4ddbd88da3118c7/lib/compiler/src/beam_asm.erl#L351-L374
            if (inflatedSize == 0L) {
                return fromInflated(data, offset)
            }

            if (inflatedSize > MAX_INFLATED_SIZE) {
                throw RefusedBeamData(
                    "LitT declares $inflatedSize inflated bytes from a ${data.size} byte chunk, " +
                        "beyond the $MAX_INFLATED_SIZE cap",
                )
            }

            val inflater = Inflater()

            try {
                inflater.setInput(data, offset, data.size - offset)
                val inflated = ByteArray(unsignedIntToInt(inflatedSize))

                if (inflater.inflate(inflated) == 0) {
                    throw RefusedBeamData("LitT does not inflate into its declared $inflatedSize bytes")
                }

                return fromInflated(inflated, 0)
            } finally {
                inflater.end()
            }
        }

        private fun fromInflated(inflated: ByteArray, offset: Int): Literals {
            var internalOffset = offset

            val (count, countByteCount) = unsignedInt(inflated, internalOffset)
            internalOffset += countByteCount

            val termList = mutableListOf<OtpErlangObject>()

            repeat(unsignedIntToInt(count)) {
                val (literal, literalByteCount) = literal(inflated, internalOffset)
                termList.add(literal)
                internalOffset += literalByteCount
            }

            return Literals(termList)
        }
    }

}
