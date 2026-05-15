package org.elixir_lang.beam

import junit.framework.TestCase

class DecompilerUnicodeEscapeTest : TestCase() {
    fun testEscapesUnescapedUnicodeCodePointSequence() {
        assertEquals(
            "docs with \\\\u{NNNNNN}",
            escapeUnicodeCodePointSequence("docs with \\u{NNNNNN}")
        )
    }

    fun testPreservesAlreadyEscapedUnicodeCodePointSequence() {
        assertEquals(
            "already escaped \\\\u{NNNNNN}",
            escapeUnicodeCodePointSequence("already escaped \\\\u{NNNNNN}")
        )
    }

    fun testEscapesMultipleSequences() {
        assertEquals(
            "a \\\\u{AAAAAA} and b \\\\u{BBBBBB}",
            escapeUnicodeCodePointSequence("a \\u{AAAAAA} and b \\u{BBBBBB}")
        )
    }
}
