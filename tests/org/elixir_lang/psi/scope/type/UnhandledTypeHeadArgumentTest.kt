package org.elixir_lang.psi.scope.type

import com.intellij.psi.util.PsiTreeUtil
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.psi.UnqualifiedParenthesesCall

/**
 * A `@type` head argument the type resolver cannot name is not a type variable, and saying nothing
 * about it is the right answer. A string literal is one such argument: it is a syntax the user can
 * type part-way through an edit, and it must not turn resolving every other type in the module into
 * an error report.
 */
class UnhandledTypeHeadArgumentTest : PlatformTestCase() {
    fun testUnnameableTypeHeadArgumentDoesNotReportAnError() {
        myFixture.configureByText(
            "unnameable.ex",
            """
            defmodule Unnameable do
              @type t("x") :: term
              @spec f() :: t<caret>()
              def f, do: nil
            end
            """.trimIndent()
        )
        val usage = PsiTreeUtil.getParentOfType(
            myFixture.file.findElementAt(myFixture.caretOffset),
            UnqualifiedParenthesesCall::class.java
        )
        assertNotNull("caret is not on a type usage", usage)

        val (_, errors) = captureLoggedErrors {
            MultiResolve.resolveResults("t", 0, false, usage!!)
        }

        assertEmpty("an unnameable head argument is not an error", errors)
    }
}
