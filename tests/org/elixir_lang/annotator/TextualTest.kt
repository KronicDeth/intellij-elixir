package org.elixir_lang.annotator

import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.elixir_lang.ElixirFileType
import org.elixir_lang.ElixirSyntaxHighlighter

/**
 * The quote delimiters of a string or char list carry the *same* attributes as its body.
 *
 * [Textual.highlightQuote] passes `node.firstChildNode` and `node.lastChildNode` the same
 * [com.intellij.openapi.editor.colors.TextAttributesKey] instance it passes the body fragments, and
 * [Highlighter] resolves that key against the global scheme once per call - so no colour scheme can give
 * the delimiters and the body different colours. This pins that, because a report of exactly that split
 * exists and reading the source gives no mechanism for it.
 *
 * Compares resolved [TextAttributes] rather than the key, because the key is not observable on an
 * annotator's `HighlightInfo` - see [Highlighter]. Each case therefore asserts the delimiters carry the
 * *string* attributes positively, not merely that they match the body: [Highlighter] emits nothing at all
 * when the annotator does not run, and "delimiter equals body" is satisfied by two ranges that are both
 * uncoloured.
 */
class TextualTest : BasePlatformTestCase() {
    fun testStringDelimitersCarryTheStringAttributes() {
        configure(
            """
            defmodule Sample do
              def run do
                "hello"
              end
            end
            """.trimIndent()
        )

        assertStringAttributesAt("opening quote", offsetOf("\"hello\""))
        assertStringAttributesAt("body", offsetOf("hello\""))
        assertStringAttributesAt("closing quote", offsetOf("\"\n"))
    }

    /**
     * The shape from the report: an interpolated string as the argument of a qualified call, so the
     * closing quote is directly followed by `)` and the body contains an interpolation. Interpolation
     * splits the body into several fragments, which is the one thing that could plausibly make the
     * delimiters take a different path from the body.
     */
    fun testInterpolatedStringDelimitersCarryTheStringAttributes() {
        configure(
            """
            defmodule Sample do
              def run(other) do
                Logger.warning("unhandled message: #{inspect(other)}")
              end
            end
            """.trimIndent()
        )

        assertStringAttributesAt("opening quote", offsetOf("\"unhandled"))
        assertStringAttributesAt("body", offsetOf("unhandled"))
        assertStringAttributesAt("closing quote", offsetOf("\")"))
    }

    fun testCharListDelimitersCarryTheCharListAttributes() {
        configure(
            """
            defmodule Sample do
              def run do
                'hello'
              end
            end
            """.trimIndent()
        )

        assertAttributesAt("opening quote", offsetOf("'hello'"), ElixirSyntaxHighlighter.CHAR_LIST)
        assertAttributesAt("body", offsetOf("hello'"), ElixirSyntaxHighlighter.CHAR_LIST)
        assertAttributesAt("closing quote", offsetOf("'\n"), ElixirSyntaxHighlighter.CHAR_LIST)
    }

    private fun configure(text: String) {
        myFixture.configureByText(ElixirFileType.INSTANCE, text)
    }

    private fun offsetOf(substring: String): Int {
        val offset = myFixture.editor.document.text.indexOf(substring)

        assertTrue("'$substring' not found in the configured source", offset >= 0)

        return offset
    }

    private fun assertStringAttributesAt(what: String, offset: Int) =
        assertAttributesAt(what, offset, ElixirSyntaxHighlighter.STRING)

    private fun assertAttributesAt(
        what: String,
        offset: Int,
        textAttributesKey: TextAttributesKey
    ) {
        val expected: TextAttributes? = EditorColorsManager
            .getInstance()
            .globalScheme
            .getAttributes(textAttributesKey)

        val covering = myFixture
            .doHighlighting()
            .filter { it.startOffset <= offset && it.endOffset > offset }

        assertTrue(
            "The $what at $offset should carry ${textAttributesKey.externalName} attributes; " +
                "${covering.size} HighlightInfo(s) cover it: " +
                covering.joinToString(" ; ") { "${it.startOffset}..${it.endOffset} ${it.forcedTextAttributes}" },
            covering.any { it.forcedTextAttributes == expected }
        )
    }
}
