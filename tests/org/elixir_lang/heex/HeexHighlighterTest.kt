package org.elixir_lang.heex

import com.intellij.testFramework.LexerTestCase
import org.elixir_lang.PlatformTestCase

/**
 * Golden test for [TemplateHighlighter] and the [org.elixir_lang.heex.html.HeexHTMLFileHighlighter]
 * it delegates to. Dumps the highlighter's token stream via the same
 * `LexerTestCase.printTokens(HighlighterIterator)` used for the plugin's other highlighter goldens,
 * covering the HTML layer, the Elixir layer, and a masked component tag.
 *
 * Uses `myFixture.editor.highlighter` rather than constructing a `TemplateHighlighter` directly -
 * `heex/file/Type.kt`'s `init` block registers it via `FileTypeEditorHighlighterProviders`, so a
 * `.html.heex` fixture's own editor already carries the real one, correctly bound to a `Document`
 * (`LexerTestCase.printTokens(HighlighterIterator)` needs `iterator.getDocument()` non-null, which a
 * bare `highlighter.setText(text)` does not provide).
 */
class HeexHighlighterTest : PlatformTestCase() {
    private fun printTokens(text: String): String {
        myFixture.configureByText("test.html.heex", text)
        return LexerTestCase.printTokens(myFixture.editor.highlighter.createIterator(0))
    }

    fun testHtmlLayer() {
        assertSameLines(
            """
            XML_START_TAG_START ('<')
            XML_TAG_NAME ('div')
            XML_TAG_END ('>')
            XML_DATA_CHARACTERS ('hi')
            XML_END_TAG_START ('</')
            XML_TAG_NAME ('div')
            XML_TAG_END ('>')

            """.trimIndent(),
            printTokens("<div>hi</div>")
        )
    }

    fun testElixirLayer() {
        val tokens = printTokens("<%= @x %>")
        // The Elixir layer highlights the expression body as real Elixir tokens (identifier, @,
        // whitespace), not as opaque HEEx Data - this is the point of TemplateHighlighter's second
        // registered layer.
        assertTrue(tokens.contains("identifier ('x')"))
        assertTrue(tokens.contains("@ ('@')"))
    }

    fun testComponentTag() {
        // The highlighter reports the unmasked name ('.button', not 'Cbutton'); only the token
        // boundaries depend on the masking underneath.
        assertSameLines(
            """
            XML_START_TAG_START ('<')
            XML_TAG_NAME ('.button')
            XML_TAG_END ('>')
            XML_DATA_CHARACTERS ('Click')
            XML_END_TAG_START ('</')
            XML_TAG_NAME ('.button')
            XML_TAG_END ('>')

            """.trimIndent(),
            printTokens("<.button>Click</.button>")
        )
    }
}
