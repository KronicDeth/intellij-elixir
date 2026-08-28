package org.elixir_lang.formatter

import org.elixir_lang.PlatformTestCase

/**
 * Pressing Enter after `->` or after a block identifier puts the caret where the clause body goes. Expected columns are
 * the ones `mix format` produces for the same construct.
 *
 * Both are built without an indent of their own, so delegating the caret to them indents against nothing and falls back
 * to the enclosing statement - the same defect [ContainerElementIndentTest] pins for a container's delimiters.
 *
 * These go through the platform's Enter handler rather than a reformat, so [org.elixir_lang.FormattingTest]'s
 * reformat-based fixtures cannot cover them.
 */
class ClauseIndentTest : PlatformTestCase() {
    private fun assertCaretColumnAfterEnter(code: String, expectedColumn: Int) {
        myFixture.configureByText("a.ex", code)

        myFixture.type('\n')

        assertEquals(expectedColumn, myFixture.editor.caretModel.logicalPosition.column)
    }

    private fun inRun(body: String) = "defmodule M do\n  def run do\n$body\n  end\nend\n"

    fun testAfterStabOperatorInCase() =
        assertCaretColumnAfterEnter(inRun("    case one do\n      :a -><caret>\n        one\n    end"), 8)

    fun testAfterStabOperatorInCond() =
        assertCaretColumnAfterEnter(inRun("    cond do\n      one -><caret>\n        two\n    end"), 8)

    fun testAfterStabOperatorInReceive() =
        assertCaretColumnAfterEnter(inRun("    receive do\n      :a -><caret>\n        one\n    end"), 8)

    fun testAfterStabOperatorInAnonymousFunctionWithMultipleClauses() =
        assertCaretColumnAfterEnter(inRun("    fn\n      :a -><caret>\n        one\n    end"), 8)

    fun testAfterStabOperatorInInlineAnonymousFunction() =
        assertCaretColumnAfterEnter(inRun("    _f = fn a -><caret>\n      a\n    end"), 6)

    fun testAfterStabOperatorInRescue() =
        assertCaretColumnAfterEnter(
            inRun("    try do\n      one\n    rescue\n      e -><caret>\n        two\n    end"),
            8
        )

    fun testAfterRescue() =
        assertCaretColumnAfterEnter(inRun("    try do\n      one\n    rescue<caret>\n      e ->\n        two\n    end"), 6)

    fun testAfterAfter() =
        assertCaretColumnAfterEnter(inRun("    try do\n      one\n    after<caret>\n      two\n    end"), 6)

    fun testAfterElse() =
        assertCaretColumnAfterEnter(
            inRun("    try do\n      one\n    else<caret>\n      :ok ->\n        two\n    end"),
            6
        )

    fun testAfterCatch() =
        assertCaretColumnAfterEnter(
            inRun("    try do\n      one\n    catch<caret>\n      :exit, _ ->\n        two\n    end"),
            6
        )
}
