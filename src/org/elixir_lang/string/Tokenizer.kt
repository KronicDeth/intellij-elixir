package org.elixir_lang.string

import java.text.Normalizer

object Tokenizer {
    sealed class Tokenized {
        // https://github.com/elixir-lang/elixir/blob/6289cd6b9685a3c63c9ea445f1672004fc713cb8/lib/elixir/unicode/tokenizer.ex#L181-L209
        data class Unvalidated(val acc: String, val rest: String, val allAsciiLetters: Boolean, val special: Set<Char>) {
            companion object {
                fun fromText(text: String, firstAsciiLetter: Boolean): Unvalidated {
                    val special = mutableSetOf<Char>()
                    var allAsciiLetters = firstAsciiLetter

                    for ((i, c) in text.withIndex()) {
                        when {
                            // https://github.com/elixir-lang/elixir/blob/6289cd6b9685a3c63c9ea445f1672004fc713cb8/lib/elixir/unicode/tokenizer.ex#L181-L187
                            (c == '!' || c == '?') -> {
                                special.add(c)

                                return Unvalidated(text.substring(0..i), text.substring(i + 1), allAsciiLetters, special)
                            }
                            // https://github.com/elixir-lang/elixir/blob/6289cd6b9685a3c63c9ea445f1672004fc713cb8/lib/elixir/unicode/tokenizer.ex#L189-L191
                            c == '@' -> {
                                special.add(c)
                                continue
                            }
                            // https://github.com/elixir-lang/elixir/blob/6289cd6b9685a3c63c9ea445f1672004fc713cb8/lib/elixir/unicode/tokenizer.ex#L195-L196
                            isAsciiStart(c) || isAsciiUpper(c) || isAsciiContinue(c) ->
                                continue
                            // https://github.com/elixir-lang/elixir/blob/6289cd6b9685a3c63c9ea445f1672004fc713cb8/lib/elixir/unicode/tokenizer.ex#L195-L196
                            isUnicodeStart(c) || isUnicodeUpper(c) || isUnicodeContinue(c) ->
                                allAsciiLetters = false
                            else -> {
                                return Unvalidated(text.substring(0 until i), text.substring(i), allAsciiLetters, special)
                            }
                        }
                    }

                    // https://github.com/elixir-lang/elixir/blob/6289cd6b9685a3c63c9ea445f1672004fc713cb8/lib/elixir/unicode/tokenizer.ex#L207-L209
                    return Unvalidated(text, "", allAsciiLetters, special)
                }
            }

            // https://github.com/elixir-lang/elixir/blob/6289cd6b9685a3c63c9ea445f1672004fc713cb8/lib/elixir/unicode/tokenizer.ex#L181-L209
            fun `continue`(): Unvalidated {
                val first = rest.firstOrNull()

                return if (first != null) {
                    when {

                        // https://github.com/elixir-lang/elixir/blob/6289cd6b9685a3c63c9ea445f1672004fc713cb8/lib/elixir/unicode/tokenizer.ex#L203
                        else -> this
                    }
                } else {
                    // https://github.com/elixir-lang/elixir/blob/6289cd6b9685a3c63c9ea445f1672004fc713cb8/lib/elixir/unicode/tokenizer.ex#L207-L209
                    copy(acc = rest, rest = "")
                }
            }

            // https://github.com/elixir-lang/elixir/blob/6289cd6b9685a3c63c9ea445f1672004fc713cb8/lib/elixir/unicode/tokenizer.ex#L211-L219
            fun validate(kind: Tokenizer.Kind): Tokenized =
                    if (allAsciiLetters || Normalizer.isNormalized(acc, Normalizer.Form.NFC)) {
                        Kind(kind, acc, rest, allAsciiLetters, special)
                    } else {
                        NotNFC(acc)
                    }
        }

        data class Kind(val kind: Tokenizer.Kind, val acc: String, val rest: String, val allAsciiLetters: Boolean, val special: Set<Char>): Tokenized()
        data class NotNFC(val acc: String) : Tokenized()
        object Empty: Tokenized()
    }

    enum class Kind {
        ALIAS,
        IDENTIFIER,
        ATOM
    }

    fun tokenize(text: String): Tokenized {
        val first = text.firstOrNull()

        // https://github.com/elixir-lang/elixir/blob/6289cd6b9685a3c63c9ea445f1672004fc713cb8/lib/elixir/unicode/tokenizer.ex#L160-L170
        return if (first != null) {
            when {
                // https://github.com/elixir-lang/elixir/blob/6289cd6b9685a3c63c9ea445f1672004fc713cb8/lib/elixir/unicode/tokenizer.ex#L160-L161
                isAsciiUpper(first) -> {
                    Tokenized.Unvalidated.fromText(text, firstAsciiLetter = true).validate(Kind.ALIAS)
                }
                // https://github.com/elixir-lang/elixir/blob/6289cd6b9685a3c63c9ea445f1672004fc713cb8/lib/elixir/unicode/tokenizer.ex#L160-L161
                isAsciiStart(first) -> {
                    Tokenized.Unvalidated.fromText(text, firstAsciiLetter = true).validate(Kind.IDENTIFIER)
                }
                // https://github.com/elixir-lang/elixir/blob/6289cd6b9685a3c63c9ea445f1672004fc713cb8/lib/elixir/unicode/tokenizer.ex#L166-L167
                isUnicodeUpper(first) -> {
                    Tokenized.Unvalidated.fromText(text, firstAsciiLetter = false).validate(Kind.ATOM)
                }
                // https://github.com/elixir-lang/elixir/blob/6289cd6b9685a3c63c9ea445f1672004fc713cb8/lib/elixir/unicode/tokenizer.ex#L169-L170
                isUnicodeStart(first) -> {
                    Tokenized.Unvalidated.fromText(text, firstAsciiLetter = false).validate(Kind.IDENTIFIER)
                }
                else -> Tokenized.Empty
            }
        } else {
            // https://github.com/elixir-lang/elixir/blob/6289cd6b9685a3c63c9ea445f1672004fc713cb8/lib/elixir/unicode/tokenizer.ex#L172-L173
            Tokenized.Empty
        }
    }

    // https://github.com/elixir-lang/elixir/blob/6289cd6b9685a3c63c9ea445f1672004fc713cb8/lib/elixir/unicode/tokenizer.ex#L101
    private fun isAsciiUpper(c: Char): Boolean = c in 'A'..'Z'
    // https://github.com/elixir-lang/elixir/blob/6289cd6b9685a3c63c9ea445f1672004fc713cb8/lib/elixir/unicode/tokenizer.ex#L103-L104
    private fun isAsciiStart(c: Char): Boolean = c == '_' || c in 'a'..'z'
    // https://github.com/elixir-lang/elixir/blob/6289cd6b9685a3c63c9ea445f1672004fc713cb8/lib/elixir/unicode/tokenizer.ex#L106
    private fun isAsciiContinue(c: Char): Boolean = c in '0'..'9'

    private fun isUnicodeUpper(c: Char): Boolean = c.isUpperCase()
    private fun isUnicodeStart(c: Char): Boolean = Character.isUnicodeIdentifierStart(c)
    private fun isUnicodeContinue(c: Char): Boolean = Character.isUnicodeIdentifierPart(c)
}
