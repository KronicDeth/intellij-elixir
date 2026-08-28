package org.elixir_lang.formatter

import org.elixir_lang.PlatformTestCase

/**
 * Pressing Enter inside a container puts the caret where an element goes. Expected columns are the ones `mix format`
 * produces for the same construct.
 *
 * Pinned here is the case that works: after an element, delegating to that element's block inherits a real indent.
 * After a delimiter it does not, and that is fixed for parentheses only - see [ParenthesesArgumentsIndentTest]. Lists,
 * tuples, maps, structs, bit strings, keyword lists, multiple aliases and map updates still put the caret at the
 * enclosing statement instead of the element column.
 *
 * Two fixes for those have been measured and rejected: `Indent.getNormalIndent(true)` resolves against the answering
 * block's own first leaf, which for `_list = [` is the `[` rather than the line, and giving the delimiters an indent
 * where they are built changes reformatting and breaks ten [org.elixir_lang.FormattingTest] fixtures.
 *
 * These go through the platform's Enter handler rather than a reformat, so [org.elixir_lang.FormattingTest]'s
 * reformat-based fixtures cannot cover them.
 */
class ContainerElementIndentTest : PlatformTestCase() {
    private fun assertCaretColumnAfterEnter(code: String, expectedColumn: Int) {
        myFixture.configureByText("a.ex", code)

        myFixture.type('\n')

        assertEquals(expectedColumn, myFixture.editor.caretModel.logicalPosition.column)
    }

    private fun inRun(body: String) = "defmodule M do\n  def run do\n$body\n  end\nend\n"

    fun testAfterElementInList() =
        assertCaretColumnAfterEnter(inRun("    _list = [\n      one<caret>\n    ]"), 6)

    fun testAfterElementInCall() =
        assertCaretColumnAfterEnter(inRun("    some_call(\n      one<caret>\n    )"), 6)

    /** A call nested in a list indents from the call, which the parentheses fix already gets right. */
    fun testAfterCommaInCallNestedInList() =
        assertCaretColumnAfterEnter(inRun("    _l = [\n      some_call(\n        one,<caret>\n      )\n    ]"), 8)

    fun testDoBlockBody() = assertCaretColumnAfterEnter("defmodule M do\n  def run do<caret>\n  end\nend\n", 4)
}
