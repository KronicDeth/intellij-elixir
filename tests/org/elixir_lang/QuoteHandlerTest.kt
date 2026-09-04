package org.elixir_lang

/**
 * Typing a heredoc promoter auto-closes it on the next line. The terminator's column is the dedent applied to the body,
 * and the expected columns are the ones `mix format` produces - the indent of the promoter's own line, which is not
 * always the enclosing statement's.
 *
 * The insert never reaches the formatter, so [org.elixir_lang.FormattingTest]'s reformat fixtures cannot cover it.
 */
class QuoteHandlerTest : PlatformTestCase() {
    private fun assertTextAfterTyping(code: String, typed: String, expected: String) {
        myFixture.configureByText("a.ex", code)

        myFixture.type(typed)

        assertEquals(expected, myFixture.editor.document.text)
    }

    fun testHeredocPromoterAtColumnZero() =
        assertTextAfterTyping("_x = <caret>\n", "\"\"\"", "_x = \"\"\"\n\"\"\"\n")

    fun testHeredocPromoterInFunctionBody() =
        assertTextAfterTyping(
            "defmodule M do\n  def run do\n    _x = <caret>\n  end\nend\n",
            "\"\"\"",
            "defmodule M do\n  def run do\n    _x = \"\"\"\n    \"\"\"\n  end\nend\n"
        )

    fun testHeredocPromoterInNestedBlock() =
        assertTextAfterTyping(
            "defmodule M do\n  def run do\n    if one do\n      _x = <caret>\n    end\n  end\nend\n",
            "\"\"\"",
            "defmodule M do\n  def run do\n    if one do\n      _x = \"\"\"\n      \"\"\"\n    end\n  end\nend\n"
        )

    /** The promoter's line is indented deeper than the statement it belongs to, and `mix format` follows the promoter. */
    fun testHeredocPromoterOnKeywordLine() =
        assertTextAfterTyping(
            "defmodule M do\n  def run do\n    _x = [\n      k: <caret>\n    ]\n  end\nend\n",
            "\"\"\"",
            "defmodule M do\n  def run do\n    _x = [\n      k: \"\"\"\n      \"\"\"\n    ]\n  end\nend\n"
        )

    fun testCharListHeredocPromoter() =
        assertTextAfterTyping("  _x = <caret>\n", "'''", "  _x = '''\n  '''\n")

    fun testSigilHeredocPromoter() =
        assertTextAfterTyping("  _x = ~s<caret>\n", "\"\"\"", "  _x = ~s\"\"\"\n  \"\"\"\n")

    fun testTabIndentedHeredocPromoter() =
        assertTextAfterTyping("\t_x = <caret>\n", "\"\"\"", "\t_x = \"\"\"\n\t\"\"\"\n")

    fun testLinePromoterIsUnaffected() = assertTextAfterTyping("  _x = <caret>\n", "\"", "  _x = \"\"\n")
}
