package org.elixir_lang.beam

/**
 * The entry count a chunk header declares, refused when the bytes after the header could not hold it.
 *
 * [bytesPerEntry] must be the SMALLEST an entry can be, or real files are refused: line items are
 * compact-term encoded and can be a single byte, where an export is three unsigned ints.
 */
fun declaredCount(declared: Long, availableBytes: Int, bytesPerEntry: Int, context: String): Int =
    if (declared > availableBytes / bytesPerEntry) {
        throw RefusedBeamData("$context declares $declared entries, more than $availableBytes bytes can hold")
    } else {
        declared.toInt()
    }
