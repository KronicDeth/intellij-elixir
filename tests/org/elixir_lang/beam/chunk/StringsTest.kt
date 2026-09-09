package org.elixir_lang.beam.chunk

import org.elixir_lang.beam.chunk.code.Operation
import org.elixir_lang.beam.term.Literal
import org.junit.Assert
import org.junit.Test
import org.elixir_lang.beam.chunk.code.operation.Code as OpCode

class StringsTest {
    /** A literal decodes up to `Int.MAX_VALUE`, so an offset plus a length can wrap negative. */
    @Test
    fun aStringReferencePastTheEndOfThePoolIsSkipped() {
        val putString = Operation(OpCode.BS_PUT_STRING, listOf(Literal(Int.MAX_VALUE), Literal(1)))

        Assert.assertEquals(emptyList<Strings.Entry>(), Strings("abcdef").entries(Code(listOf(putString))))
    }
}
