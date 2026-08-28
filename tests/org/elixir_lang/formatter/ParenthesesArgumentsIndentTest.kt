package org.elixir_lang.formatter

import org.elixir_lang.PlatformTestCase

/**
 * Pressing Enter inside a call's parentheses puts the caret where an argument goes, whether or not the parentheses
 * already hold one.
 *
 * These go through the platform's Enter handler rather than a reformat, so [FormattingTest]'s reformat-based fixtures
 * cannot cover them.
 */
class ParenthesesArgumentsIndentTest : PlatformTestCase() {
    private fun assertCaretColumnAfterEnter(code: String, expectedColumn: Int) {
        myFixture.configureByText("a.ex", code)

        myFixture.type('\n')

        assertEquals(expectedColumn, myFixture.editor.caretModel.logicalPosition.column)
    }

    fun testEmptyParentheses() {
        assertCaretColumnAfterEnter("defp visit_file(<caret>) do\nend\n", 7)
    }

    fun testEmptyParenthesesWithoutDoBlock() {
        assertCaretColumnAfterEnter("defp visit_file(<caret>)\n", 7)
    }

    fun testEmptyParenthesesOfIndentedCall() {
        assertCaretColumnAfterEnter("defp visit_file(path) do\n  other(<caret>)\nend\n", 4)
    }

    fun testBeforeArgument() {
        assertCaretColumnAfterEnter("defp visit_file(<caret>path) do\nend\n", 7)
    }

    fun testAfterComma() {
        assertCaretColumnAfterEnter("defp visit_file(path,<caret>acc) do\nend\n", 7)
    }

    fun testAfterLastArgument() {
        assertCaretColumnAfterEnter("defp visit_file(path<caret>) do\nend\n", 5)
    }
}
