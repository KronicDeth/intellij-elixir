package org.elixir_lang.formatter

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.psi.codeStyle.CodeStyleManager
import org.elixir_lang.PlatformTestCase

/**
 * Does `Code > Auto-Indent Lines` actually indent Elixir?
 *
 * https://github.com/KronicDeth/intellij-elixir/issues/1077 reports it doing nothing in WebStorm, with
 * no reproduction ever supplied. Nothing under `tests/` exercised this action, so its behaviour was
 * simply unasserted rather than known-good or known-broken. These tests establish which.
 *
 * The action runs `CodeStyleManager.adjustLineIndent`, the same entry point the platform's paste
 * handler uses - a different path from the whole-file `reformat` the existing formatter fixtures
 * cover.
 *
 * Tests run on IntelliJ IDEA (`IC`/`IU`), so this cannot speak to WebStorm specifically. What it can
 * show is whether the Elixir side of the action works at all: the plugin's `lang.formatter`
 * registration is not IDE-conditional, so if it works here the language support is not the missing
 * piece.
 */
class AutoIndentLinesTest : PlatformTestCase() {
    /** Indent the line containing <caret>, and return the whole file. */
    private fun autoIndent(code: String): String {
        myFixture.configureByText("a.ex", code)

        WriteCommandAction.runWriteCommandAction(project) {
            CodeStyleManager.getInstance(project).adjustLineIndent(myFixture.file, myFixture.caretOffset)
        }

        return myFixture.file.text
    }

    fun testIndentsAFunctionBodyFromColumnZero() {
        val actual = autoIndent("defmodule M do\n  def f do\n<caret>:ok\n  end\nend\n")

        assertEquals("defmodule M do\n  def f do\n    :ok\n  end\nend\n", actual)
    }

    fun testOutdentsAnOverIndentedFunctionBody() {
        val actual = autoIndent("defmodule M do\n  def f do\n            <caret>:ok\n  end\nend\n")

        assertEquals("defmodule M do\n  def f do\n    :ok\n  end\nend\n", actual)
    }

    fun testIndentsADefFromColumnZero() {
        val actual = autoIndent("defmodule M do\n<caret>def f do\n    :ok\n  end\nend\n")

        assertEquals("defmodule M do\n  def f do\n    :ok\n  end\nend\n", actual)
    }

    fun testIndentsInsideACase() {
        val actual = autoIndent("defmodule M do\n  def f(x) do\n    case x do\n<caret>:a -> 1\n    end\n  end\nend\n")

        assertEquals("defmodule M do\n  def f(x) do\n    case x do\n      :a -> 1\n    end\n  end\nend\n", actual)
    }

    fun testLeavesAlreadyCorrectIndentationAlone() {
        val code = "defmodule M do\n  def f do\n    <caret>:ok\n  end\nend\n"

        assertEquals(code.replace("<caret>", ""), autoIndent(code))
    }
}
