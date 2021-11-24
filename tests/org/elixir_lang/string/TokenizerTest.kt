package org.elixir_lang.string

import junit.framework.TestCase

class TokenizerTest : TestCase() {
    fun testStartsWithDigit() {
        assertEquals(Tokenizer.Tokenized.Empty, Tokenizer.tokenize("4k"))
    }

    fun testContainsHyphen() {
        assertEquals(
                Tokenizer.Tokenized.Kind(
                        kind = Tokenizer.Kind.IDENTIFIER,
                        acc = "csrf",
                        rest = "-params",
                        allAscii = true,
                        special = emptySet()
                ),
                Tokenizer.tokenize("csrf-params"),
        )
    }

    // https://github.com/elixir-lang/elixir/blob/58518794306c70204de14f9ed214fb7f296769d9/lib/elixir/test/elixir/kernel/string_tokenizer_test.exs#L21-L40
    fun testTokenizesAtoms() {
        assertIdentifier("_12", allAscii = true)
        assertIdentifier("ola", allAscii = true)
        assertIdentifier("ólá", allAscii = false)
        assertIdentifier("ólá?", allAscii = false, special = setOf('?'))
        assertIdentifier("ólá!", allAscii = false, special = setOf('!'))
        assertIdentifier("ól@", allAscii = false, special = setOf('@'))
        assertIdentifier("ól@!", allAscii = false, special = setOf('@', '!'))
        assertIdentifier("ó@@!", allAscii = false, special = setOf('@', '!'))
        assertKind(kind = Tokenizer.Kind.ALIAS, "Ola", allAscii = true)
        assertAtom("Ólá", allAscii = false)
        assertAtom("ÓLÁ", allAscii = false)
        assertAtom("ÓLÁ?", allAscii = false, special = setOf('?'))
        assertAtom("ÓLÁ!", allAscii = false, special = setOf('!'))
        assertAtom("ÓL@!", allAscii = false, special = setOf('@', '!'))
        assertAtom("Ó@@!", allAscii = false, special = setOf('@', '!'))
        assertIdentifier("こんにちは世界", allAscii = false)
        assertEmpty( "123")
        assertEmpty("@123")
    }

    private fun assertIdentifier(text: String, allAscii: Boolean, special: Set<Char> = emptySet()) {
        assertKind(kind = Tokenizer.Kind.IDENTIFIER, text, allAscii, special)
    }

    private fun assertAtom(text: String, allAscii: Boolean, special: Set<Char> = emptySet()) {
        assertKind(kind = Tokenizer.Kind.ATOM, text, allAscii, special)
    }

    private fun assertKind(kind: Tokenizer.Kind, text: String, allAscii: Boolean, special: Set<Char> = emptySet()) {
        assertEquals(
                Tokenizer.Tokenized.Kind(
                        kind,
                        acc = text,
                        rest = "",
                        allAscii,
                        special
                ),
                Tokenizer.tokenize(text),
        )
    }

    private fun assertEmpty(text: String) {
        assertEquals(
                Tokenizer.Tokenized.Empty,
                Tokenizer.tokenize(text),
        )
    }
}
