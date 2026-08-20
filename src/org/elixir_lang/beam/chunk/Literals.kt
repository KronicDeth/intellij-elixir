package org.elixir_lang.beam.chunk

import com.ericsson.otp.erlang.OtpErlangObject
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.component1
import com.intellij.openapi.util.component2
import org.elixir_lang.beam.chunk.Chunk.Companion.unsignedInt
import org.elixir_lang.beam.chunk.literals.literal
import org.elixir_lang.beam.term.unsignedIntToInt
import java.util.zip.DataFormatException
import java.util.zip.Inflater
import kotlin.math.min

class Literals(private val termList: List<OtpErlangObject>) {
    operator fun get(index: Int): OtpErlangObject = termList[index]
    fun getOrNull(index: Int): OtpErlangObject? = termList.getOrNull(index)
    fun size(): Int = termList.size

    companion object {
        val LOGGER = Logger.getInstance(Literals::class.java)

        fun from(chunk: Chunk): Literals? {
            val data = chunk.data
            var offset = 0

            val (inflatedSize, inflatedSizeByteCount) = unsignedInt(data, offset)
            offset += inflatedSizeByteCount

            // OTP 28+ uses uncompressed literals and stores a zero size sentinel.
            // see https://github.com/erlang/otp/blob/f80e9c1c4a3f271e39fcdb0be4ddbd88da3118c7/lib/compiler/src/beam_asm.erl#L351-L374
            if (inflatedSize == 0L) {
                return safeFromInflated(data, offset, "uncompressed")
            }

            val inflater = Inflater()
            val compressedSize = data.size - offset
            inflater.setInput(data, offset, compressedSize)
            val inflated = ByteArray(unsignedIntToInt(inflatedSize))
            val actualInflatedSize = try {
                inflater.inflate(inflated)
            } catch (exception: DataFormatException) {
                val availableBytes = data.size - offset
                val wordCount = min(8, availableBytes / 4)
                val words32be = if (wordCount > 0) {
                    (0 until wordCount).joinToString(", ") { index ->
                        unsignedInt(data, offset + index * 4).first.toString()
                    }
                } else {
                    "<none>"
                }
                LOGGER.error(
                    "Could not inflate LitT. " +
                        "compressedPreviewBytes=${previewHex(offset, inflated)}, " +
                        "firstWords32be=[$words32be], " +
                        "inflatedSize=$inflatedSize, " +
                        "compressedSize=$compressedSize, " +
                        "chunkSize=${data.size}",
                    exception
                )
                inflater.end()
                return null
            }

            return if (actualInflatedSize != 0) {
                inflater.end()

                safeFromInflated(inflated, 0, "inflated")
            } else {
                LOGGER.error(
                        "Could not inflate LitT into $inflatedSize bytes.\n" +
                                "needsDictionary = ${inflater.needsDictionary()}\n" +
                                "needsInput is ${inflater.needsDictionary()}"
                )
                inflater.end()

                null
            }
        }

        private fun safeFromInflated(inflated: ByteArray, offset: Int, context: String): Literals? =
            try {
                fromInflated(inflated, offset)
            } catch (exception: Exception) {
                LOGGER.error(
                    "Could not parse LitT ($context). " +
                        "previewBytes=${previewHex(offset, inflated)}, " +
                        "chunkSize=${inflated.size}, " +
                        "offset=$offset",
                    exception
                )
                null
            }

        private fun previewHex(offset: Int, inflated: ByteArray) = if (offset < inflated.size) {
            val previewLimit = min(offset + 64, inflated.size)
            inflated.copyOfRange(offset, previewLimit).joinToString(" ") { "%02X".format(it) }
        } else {
            "<empty>"
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
