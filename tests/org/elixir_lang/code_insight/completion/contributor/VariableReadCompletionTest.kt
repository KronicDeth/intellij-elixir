package org.elixir_lang.code_insight.completion.contributor

import org.elixir_lang.PlatformTestCase
import org.elixir_lang.code_insight.completionStringsAtCaret

/** Completion offers what is bound in scope, not every name read on the way there. */
class VariableReadCompletionTest : PlatformTestCase() {
    fun testReadInsideAnEarlierCallArgumentIsNotOffered() {
        myFixture.configureByText(
            "read.ex",
            "defmodule Read do\n  def run(bound) do\n    IO.puts(unbound_name)\n    IO.puts(bound)\n    unbo<caret>\n  end\nend\n"
        )

        val strings = myFixture.completionStringsAtCaret()
        val text = myFixture.editor.document.text

        // a lone candidate is inserted without a popup, so the document is checked as well as the popup
        assertFalse("a read with no binding was inserted:\n$text", text.contains("    unbound_name\n  end"))
        assertFalse("a read with no binding was offered: $strings", strings?.contains("unbound_name") == true)
    }

    fun testBindingReadInsideAnEarlierCallArgumentIsStillOffered() {
        myFixture.configureByText(
            "bound.ex",
            "defmodule Bound do\n  def run(bound_name) do\n    IO.puts(bound_name)\n    bou<caret>\n  end\nend\n"
        )

        myFixture.completionStringsAtCaret()
        val text = myFixture.editor.document.text

        assertTrue("the parameter was not completed:\n$text", text.contains("    bound_name\n  end"))
    }
}
