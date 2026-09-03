package org.elixir_lang.eex.file

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.psi.codeStyle.CodeStyleManager
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.code_insight.completionStringsAtCaret

/**
 * https://github.com/KronicDeth/intellij-elixir/issues/1756
 *
 * The editor services an `*.html.eex` gets for its markup half - reformatting, the Enter handler,
 * quote auto-closing and HTML completion - all resolve through the data language the template's view
 * provider derives, so each of them is really asking whether the HTML root wins at the caret.
 *
 * Every assertion is paired with the same gesture in a plain `.html` file and asserts the two agree,
 * rather than asserting a literal that a code-style default could shift. That control is
 * load-bearing: an earlier attempt on this path drove an action that turned out to do nothing in the
 * control either, which reads as a confirmed defect when it is really measuring the harness.
 *
 * `.leex` is a separate file type on the same `EEx` language, so it inherits the same registrations
 * and is pinned here too - nothing else in the suite asserts it.
 */
class EditorServicesTest : PlatformTestCase() {
    fun testReformatIndentsMarkupTheSameAsAPlainHtmlFile() {
        val expected = reformatted("control.html", UNINDENTED)

        assertTrue("the control did not indent at all, so nothing else here is meaningful", expected != UNINDENTED)
        assertEquals(expected, reformatted("page.html.eex", UNINDENTED))
    }

    fun testReformatIndentsMarkupInALeexFile() {
        val expected = reformatted("control.html", UNINDENTED)

        assertTrue("the control did not indent at all, so nothing else here is meaningful", expected != UNINDENTED)
        assertEquals(expected, reformatted("page.html.leex", UNINDENTED))
    }

    fun testEnterAfterAnOpeningTagIndentsTheSameAsAPlainHtmlFile() {
        val expected = caretColumnAfterEnter("control.html", "<div><caret></div>")

        assertTrue("the control put the caret at column 0, so it is not indenting either", expected > 0)
        assertEquals(expected, caretColumnAfterEnter("page.html.eex", "<div><caret></div>"))
    }

    fun testTypingAnOpeningQuoteClosesItTheSameAsAPlainHtmlFile() {
        val expected = afterTyping("control.html", "<div class=<caret>></div>", '"')

        assertTrue(
            "the control did not auto-close the quote, so nothing else here is meaningful",
            expected.contains("\"\"")
        )
        assertEquals(expected, afterTyping("page.html.eex", "<div class=<caret>></div>", '"'))
    }

    fun testHtmlTagCompletionOffersTheSameAsAPlainHtmlFile() {
        val expected = completions("control.html", TAG_PREFIX)

        assertTrue(
            "the control offered no HTML tags, so nothing else here is meaningful",
            expected.orEmpty().contains("table")
        )
        assertEquals(expected, completions("page.html.eex", TAG_PREFIX))
    }

    fun testHtmlTagCompletionOffersTheSameInALeexFile() {
        val expected = completions("control.html", TAG_PREFIX)

        assertTrue(
            "the control offered no HTML tags, so nothing else here is meaningful",
            expected.orEmpty().contains("table")
        )
        assertEquals(expected, completions("page.html.leex", TAG_PREFIX))
    }

    fun testHtmlAttributeCompletionOffersTheSameAsAPlainHtmlFile() {
        val expected = completions("control.html", ATTRIBUTE_PREFIX)

        assertTrue(
            "the control offered no HTML attributes, so nothing else here is meaningful",
            expected.orEmpty().contains("class")
        )
        assertEquals(expected, completions("page.html.eex", ATTRIBUTE_PREFIX))
    }

    private fun reformatted(fileName: String, text: String): String {
        myFixture.configureByText(fileName, text)

        WriteCommandAction.runWriteCommandAction(project) {
            CodeStyleManager.getInstance(project).reformat(myFixture.file)
        }

        return myFixture.file.text
    }

    private fun caretColumnAfterEnter(fileName: String, text: String): Int {
        myFixture.configureByText(fileName, text)

        myFixture.type('\n')

        return myFixture.editor.caretModel.logicalPosition.column
    }

    private fun afterTyping(fileName: String, text: String, character: Char): String {
        myFixture.configureByText(fileName, text)

        myFixture.type(character)

        return myFixture.editor.document.text
    }

    private fun completions(fileName: String, text: String): List<String>? {
        myFixture.configureByText(fileName, text)

        return myFixture.completionStringsAtCaret()
    }

    companion object {
        private const val UNINDENTED = "<div>\n<span>a</span>\n</div>\n"
        private const val TAG_PREFIX = "<div>\n  <ta<caret>\n</div>"
        // A prefix short enough that several attributes match - a lone candidate auto-inserts instead
        // of opening the popup, which leaves nothing to compare.
        private const val ATTRIBUTE_PREFIX = "<div c<caret>></div>"
    }
}
