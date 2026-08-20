package org.elixir_lang.beam.chunk

import org.elixir_lang.beam.chunk.code.operation.Code as OpCode
import org.elixir_lang.beam.term.Literal
import java.nio.charset.Charset

/* The StrT chunk itself does not track the location of strings.  The Code section keeps track of offsets and lengths,
   so that that substring can be used of larger sections of the pool. */
class Strings(val pool: String) {
    /**
     * Extracts individual strings from the pool using (offset, length) references from the Code chunk.
     *
     * The StrT pool does not contain separators - the Code section's `bs_put_string` and `bs_match_string`
     * instructions reference substrings by offset and length. Without the Code chunk, we fall back to
     * splitting on null bytes (works for some BEAM files) or showing the entire pool as one entry.
     */
    fun entries(code: Code?): List<Entry> {
        if (pool.isEmpty()) return emptyList()

        val references = code?.let { extractStringReferences(it) }

        return if (!references.isNullOrEmpty()) {
            references
                .sortedBy { it.first }
                .distinctBy { it }
                .mapNotNull { (offset, length) ->
                    val end = offset + length
                    if (offset in 0..pool.length && end <= pool.length && length > 0) {
                        Entry(offset, pool.substring(offset, end))
                    } else {
                        null
                    }
                }
        } else {
            // Fallback: split on null bytes (works for some BEAM files that null-terminate strings)
            val nullSplit = mutableListOf<Entry>()
            var offset = 0
            for (part in pool.split('\u0000')) {
                if (part.isNotEmpty()) {
                    nullSplit.add(Entry(offset, part))
                }
                // +1 for the null byte separator
                offset += part.toByteArray(Charsets.UTF_8).size + 1
            }

            if (nullSplit.size > 1) {
                nullSplit
            } else {
                // No null separators found - show entire pool as single entry
                listOf(Entry(0, pool))
            }
        }
    }

    data class Entry(val offset: Int, val value: String)

    companion object {
        fun from(chunk: Chunk): Strings = Strings(chunk.data.toString(Charset.forName("UTF-8")))

        /**
         * Extracts (offset, length) pairs from BS_PUT_STRING and BS_MATCH_STRING operations in the Code chunk.
         */
        private fun extractStringReferences(code: Code): List<Pair<Int, Int>> {
            val refs = mutableSetOf<Pair<Int, Int>>()

            for (i in 0 until code.size()) {
                val op = code[i]
                when (op.code) {
                    OpCode.BS_PUT_STRING -> {
                        // termList[0] = length, termList[1] = pool_offset
                        val length = (op.termList.getOrNull(0) as? Literal)?.index
                        val offset = (op.termList.getOrNull(1) as? Literal)?.index
                        if (length != null && offset != null && length > 0) {
                            refs.add(Pair(offset, length))
                        }
                    }
                    OpCode.BS_MATCH_STRING -> {
                        // termList[2] = bit_length, termList[3] = pool_offset
                        val bitLength = (op.termList.getOrNull(2) as? Literal)?.index
                        val offset = (op.termList.getOrNull(3) as? Literal)?.index
                        if (bitLength != null && offset != null && bitLength > 0) {
                            val byteLength = (bitLength + 7) / 8
                            refs.add(Pair(offset, byteLength))
                        }
                    }
                    else -> { /* skip */ }
                }
            }

            return refs.toList()
        }
    }
}
