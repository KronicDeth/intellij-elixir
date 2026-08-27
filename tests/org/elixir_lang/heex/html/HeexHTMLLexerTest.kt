package org.elixir_lang.heex.html

import com.intellij.psi.xml.XmlTokenType
import org.elixir_lang.PlatformTestCase

/**
 * Unit tests for [HeexHTMLLexer.maskRelativeComponentDots] (private, exercised indirectly through
 * [HeexHTMLLexer.start]) - the HTML lexer has no extension point for a tag name starting with `.`
 * (`intellij-community/xml/xml-parser/src/com/intellij/lexer/_HtmlLexer.flex` hard-codes
 * `TAG_NAME`'s first character as letter/`_`/`:`), so `<.button>` is masked to `<Cbutton>` before
 * being handed to the platform lexer. Masking is length-preserving so token offsets line up with
 * the unmasked source - `HeexHTMLFileElementType.parseContents` builds its `PsiBuilder` over the
 * chameleon's own (unmasked) chars, relying on this lexer only for token boundaries.
 *
 * Needs a live `Application` (`BasePlatformTestCase`, not the plain-JUnit `flex_lexer.Test`
 * harness): `HtmlLexer`/`BaseHtmlLexer`'s constructor and `start` reach the
 * `HtmlEmbeddedContentSupport` extension point.
 */
class HeexHTMLLexerTest : PlatformTestCase() {
    private fun tokens(lexer: HeexHTMLLexer, text: CharSequence, startOffset: Int = 0, endOffset: Int = text.length): List<Triple<com.intellij.psi.tree.IElementType, String, IntRange>> {
        lexer.start(text, startOffset, endOffset, 0)
        val result = mutableListOf<Triple<com.intellij.psi.tree.IElementType, String, IntRange>>()
        while (lexer.tokenType != null) {
            result.add(Triple(lexer.tokenType!!, lexer.tokenText, lexer.tokenStart..lexer.tokenEnd))
            lexer.advance()
        }
        return result
    }

    fun testOpeningRelativeComponentTagMasksToLegalTagName() {
        // The HTML lexer reports both the start and end tag's name as XML_NAME (there is a
        // separate XML_TAG_NAME element type, but the lexer itself never emits it - that type is
        // only assembled by the parser).
        val tagNameTokens = tokens(HeexHTMLLexer(), "<.button>content</.button>")
            .filter { (type, _, _) -> type == XmlTokenType.XML_NAME }

        // Both the opening and closing tag name mask the leading `.` to `C` - the dot itself never
        // reaches the platform lexer as part of a name token.
        assertEquals(listOf("Cbutton", "Cbutton"), tagNameTokens.map { (_, text, _) -> text })
    }

    fun testPlainTagsAreUnaffected() {
        val tagNameTokens = tokens(HeexHTMLLexer(), "<button>content</button>")
            .filter { (type, _, _) -> type == XmlTokenType.XML_NAME }

        assertEquals(listOf("button", "button"), tagNameTokens.map { (_, text, _) -> text })
    }

    fun testMaskingPreservesLength() {
        val text = "<.button one={\"\"} class=\"\">content</.button>"
        val lexed = tokens(HeexHTMLLexer(), text)

        // The masked buffer is character-for-character the same length as the input - only `.` -> `C`
        // substitutions happen, never an insertion or deletion - so token offsets stay valid indices
        // into the original text.
        assertEquals(text.length, lexed.last().third.last)
    }

    /**
     * `HeexHTMLLexer.start` masks the whole buffer (masking is length-preserving, so every index
     * stays valid) and passes `startOffset`/`endOffset` through unchanged to `super.start`, so
     * `getTokenStart()`/`getTokenEnd()` report positions in the original buffer's coordinate space
     * even when the lex does not start at 0 - the case an incremental re-lex depends on.
     */
    fun testTokenOffsetsAreAbsoluteWhenStartOffsetIsNonZero() {
        val prefix = "before"
        val tag = "<.button>"
        val text = prefix + tag
        val lexed = tokens(HeexHTMLLexer(), text, startOffset = prefix.length, endOffset = text.length)

        val firstToken = lexed.first()
        assertEquals(
            "token start should be in the ORIGINAL buffer's coordinate space, not rebased to the " +
                "startOffset..endOffset slice",
            prefix.length,
            firstToken.third.first
        )
    }

    override fun getTestDataPath(): String = "testData/org/elixir_lang/heex/html"
}
