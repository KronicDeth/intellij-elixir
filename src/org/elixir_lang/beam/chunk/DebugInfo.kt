package org.elixir_lang.beam.chunk

import com.ericsson.otp.erlang.OtpErlangObject
import org.elixir_lang.beam.binaryToTerm

class DebugInfo(val term: OtpErlangObject) {
    companion object {
        fun from(chunk: Chunk): DebugInfo {
            val data = chunk.data
            var offset = 0

            val (term, termByteCount) = binaryToTerm(data, offset)
            offset += termByteCount

            val dataSize = data.size
            assert(termByteCount == dataSize) {
                "Expected Dbgi binary_to_term binary (size $termByteCount) to fill the whole chunk (size $dataSize)"
            }

            return DebugInfo(term)
        }
    }
}
