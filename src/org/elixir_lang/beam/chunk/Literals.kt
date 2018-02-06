package org.elixir_lang.beam.chunk

import com.ericsson.otp.erlang.OtpErlangObject
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.component1
import com.intellij.openapi.util.component2
import org.elixir_lang.beam.chunk.Chunk.unsignedInt
import org.elixir_lang.beam.chunk.literals.literal
import org.elixir_lang.beam.term.unsignedIntToInt
import java.util.zip.Inflater

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

            val inflater = Inflater()
            inflater.setInput(data, offset, data.size - offset)
            val inflated = ByteArray(unsignedIntToInt(inflatedSize))
            val actualInflatedSize = inflater.inflate(inflated)

            return if (actualInflatedSize != 0) {
                inflater.end()

                fromInflated(inflated, 0)
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

        private fun fromInflated(inflated: ByteArray, offset: Int): Literals? {
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
