package org.elixir_lang.beam.chunk.literals

import com.ericsson.otp.erlang.OtpErlangObject
import com.intellij.openapi.util.component1
import com.intellij.openapi.util.component2
import org.elixir_lang.beam.binaryToTerm
import org.elixir_lang.beam.chunk.Chunk.Companion.unsignedInt
import org.elixir_lang.beam.term.ByteCount
import org.elixir_lang.beam.term.unsignedIntToInt

fun literal(data: ByteArray, offset: Int): Pair<OtpErlangObject, ByteCount> {
    var internalOffset = offset

    val (expectedTermByteCount, expectedTermByteCountByteCount) = unsignedInt(data, internalOffset)
    internalOffset += expectedTermByteCountByteCount

    val (term, termByteCount) = binaryToTerm(data, internalOffset)

    assert(termByteCount == unsignedIntToInt(expectedTermByteCount)) {
        "The term byte count ($termByteCount) did not match the expected term byte count ($expectedTermByteCount) " +
                "in the LitT Literal"
    }

    internalOffset += termByteCount

    return Pair(term, internalOffset - offset)
}
