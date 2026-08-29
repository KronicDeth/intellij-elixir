package org.elixir_lang.formatter

import com.intellij.psi.codeStyle.CodeStyleSettingsManager
import org.elixir_lang.ElixirFileType
import org.elixir_lang.PlatformTestCase

/**
 * Pressing Enter inside a container puts the caret where an element goes, whether it follows an element or one of the
 * tokens punctuating the container. Expected columns are the ones `mix format` produces for the same construct.
 *
 * A delimiter is built without an indent of its own, so delegating the caret to it indents against nothing and falls
 * back to the enclosing statement. [org.elixir_lang.formatter.Block.getChildAttributes] answers those positions with
 * the element's own indent instead.
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

    fun testAfterOpeningBracket() =
        assertCaretColumnAfterEnter(inRun("    _list = [<caret>\n      one\n    ]"), 6)

    fun testAfterCommaInList() =
        assertCaretColumnAfterEnter(inRun("    _list = [\n      one,<caret>\n    ]"), 6)

    fun testAfterCommaInKeywordList() =
        assertCaretColumnAfterEnter(inRun("    _list = [\n      a: 1,<caret>\n      b: 2\n    ]"), 6)

    fun testAfterOpeningCurlyOfTuple() =
        assertCaretColumnAfterEnter(inRun("    _tuple = {<caret>\n      one\n    }"), 6)

    fun testAfterCommaInTuple() =
        assertCaretColumnAfterEnter(inRun("    _tuple = {\n      one,<caret>\n    }"), 6)

    fun testAfterOpeningCurlyOfMap() =
        assertCaretColumnAfterEnter(inRun("    _map = %{<caret>\n      one: 1\n    }"), 6)

    fun testAfterCommaBetweenMapKeywordPairs() =
        assertCaretColumnAfterEnter(inRun("    _map = %{\n      one: 1,<caret>\n    }"), 6)

    fun testAfterCommaBetweenMapAssociations() =
        assertCaretColumnAfterEnter(inRun("    _map = %{\n      :a => 1,<caret>\n      :b => 2\n    }"), 6)

    fun testAfterOpeningCurlyOfStruct() =
        assertCaretColumnAfterEnter(inRun("    _struct = %S{<caret>\n      one: 1\n    }"), 6)

    fun testAfterCommaInStruct() =
        assertCaretColumnAfterEnter(inRun("    _struct = %S{\n      one: 1,<caret>\n    }"), 6)

    fun testAfterOpeningBit() =
        assertCaretColumnAfterEnter(inRun("    _bits = <<<caret>\n      one\n    >>"), 6)

    fun testAfterCommaInBitString() =
        assertCaretColumnAfterEnter(inRun("    _bits = <<\n      one,<caret>\n    >>"), 6)

    fun testAfterOpeningCurlyOfMultipleAliases() =
        assertCaretColumnAfterEnter("defmodule M do\n  alias Foo.{<caret>\n    Bar\n  }\nend\n", 4)

    fun testAfterCommaInMultipleAliases() =
        assertCaretColumnAfterEnter("defmodule M do\n  alias Foo.{\n    Bar,<caret>\n  }\nend\n", 4)

    /** The pipe takes the first indent, so the pairs written after it take a second. */
    fun testAfterCommaBetweenMapUpdateKeywordPairs() =
        assertCaretColumnAfterEnter(inRun("    _updated = %{\n      map\n      | one: 1,<caret>\n    }"), 8)

    fun testAfterCommaBetweenMapUpdateAssociations() =
        assertCaretColumnAfterEnter(inRun("    _updated = %{\n      map\n      | :a => 1,<caret>\n    }"), 8)

    /** The pipe's own column moves with nesting, and the pairs stay one indent in from it. */
    fun testAfterCommaBetweenMapUpdateKeywordPairsNestedInAList() =
        assertCaretColumnAfterEnter(
            inRun("    _l = [\n      %{\n        map\n        | one: 1,<caret>\n      }\n    ]"),
            10
        )

    /** Written as the value of an outer update, the inner map begins on a continuation line, not its own. */
    fun testAfterCommaBetweenMapUpdateKeywordPairsNestedInAMapUpdate() =
        assertCaretColumnAfterEnter(
            inRun(
                "    _deep = %{\n      outer\n      | inner: %{\n          map\n          | one: 1,<caret>\n        }\n    }"
            ),
            12
        )

    /** A call nested in a list indents from the call, which the parentheses fix already gets right. */
    fun testAfterCommaInCallNestedInList() =
        assertCaretColumnAfterEnter(inRun("    _l = [\n      some_call(\n        one,<caret>\n      )\n    ]"), 8)

    fun testAfterCommaInListNestedInCall() =
        assertCaretColumnAfterEnter(
            inRun("    nested_call(\n      one,\n      [\n        two,<caret>\n      ]\n    )"),
            8
        )

    /**
     * `mix format` indents a container's elements from the line the container starts on, so a longer name
     * before it moves nothing. Confirmed by running `mix format` over both widths.
     */
    fun testListElementColumnDoesNotVaryWithTheNameBeforeIt() {
        assertCaretColumnAfterEnter(inRun("    _l = [\n      one,<caret>\n    ]"), 6)
        assertCaretColumnAfterEnter(inRun("    _a_much_longer_variable_name = [\n      one,<caret>\n    ]"), 6)
    }

    fun testMapUpdatePairColumnDoesNotVaryWithTheNameBeforeIt() {
        assertCaretColumnAfterEnter(inRun("    _u = %{\n      map\n      | one: 1,<caret>\n    }"), 8)
        assertCaretColumnAfterEnter(
            inRun("    _another_much_longer_name = %{\n      map\n      | one: 1,<caret>\n    }"),
            8
        )
    }

    fun testDoBlockBody() = assertCaretColumnAfterEnter("defmodule M do\n  def run do<caret>\n  end\nend\n", 4)

    /**
     * `mix format` only ever indents with spaces, so the column here is not one it produces: it is the column
     * [testAfterCommaBetweenMapUpdateKeywordPairs] expects for the same construct, written with a tab per indent
     * because `Use tab character` is a code style option a user can turn on for Elixir.
     *
     * A tab is worth however many columns it takes to reach the next tab stop, so the pipe's column and the indent it
     * is measured against are read as columns rather than as counts of characters.  Counting characters puts the caret
     * a column short of the pairs above it.
     */
    fun testAfterCommaBetweenMapUpdateKeywordPairsIndentedWithTabs() = withTabIndents {
        assertCaretColumnAfterEnter("defmodule M do\n\tdef run do\n\t\t_u = %{\n\t\t\tmap\n\t\t\t| one: 1,<caret>\n\t\t}\n\tend\nend\n", 8)
    }

    private fun withTabIndents(body: () -> Unit) {
        val settingsManager = CodeStyleSettingsManager.getInstance(project)
        val settings = settingsManager.currentSettings.clone()

        settings.getIndentOptions(ElixirFileType.INSTANCE).apply {
            USE_TAB_CHARACTER = true
            SMART_TABS = false
            TAB_SIZE = 2
            INDENT_SIZE = 2
        }

        settingsManager.setTemporarySettings(settings)

        try {
            body()
        } finally {
            settingsManager.dropTemporarySettings()
        }
    }
}
