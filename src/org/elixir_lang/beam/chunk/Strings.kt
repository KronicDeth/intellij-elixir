package org.elixir_lang.beam.chunk

import java.nio.charset.Charset

/* The StrT chunk itself does not track the location of strings.  The Code section keeps track of offsets and lengths,
   so that that substring can be used of larger sections of the pool. */
class Strings(val pool: String) {
    companion object {
        fun from(chunk: Chunk): Strings = Strings(chunk.data.toString(Charset.forName("UTF-8")))
    }
}
