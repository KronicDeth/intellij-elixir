package org.elixir_lang.reference.callable

import com.intellij.psi.PsiPolyVariantReference
import org.elixir_lang.PlatformTestCase

/**
 * A quote bound by destructuring, `{x, _} = {quote ... end, :other}`, is reached through the container but its value
 * is not a call the walk can follow, so nothing is injected. That is a limit, not an error to report.
 */
class UnquoteDestructuredBindingTest : PlatformTestCase() {
    fun testQuoteBoundThroughATupleReportsNoError() = assertNoErrorResolving(
        "{x, _} = {quote do\n      def injected(), do: :ok\n    end, :other}"
    )

    fun testQuoteBoundThroughAListReportsNoError() = assertNoErrorResolving(
        "[x] = [quote do\n      def injected(), do: :ok\n    end]"
    )

    private fun assertNoErrorResolving(binding: String) {
        myFixture.configureByText(
            "injector.ex",
            "defmodule Injector do\n  defmacro __using__(_opts) do\n    $binding\n    quote do\n      unquote(x)\n    end\n  end\nend\n"
        )
        myFixture.configureByText("user.ex", "defmodule User do\n  use Injector\n  def call, do: <caret>injected()\nend\n")
        val reference = myFixture.file.findReferenceAt(myFixture.caretOffset)!! as PsiPolyVariantReference

        val (_, errors) = captureLoggedErrors { reference.multiResolve(false) }

        assertEmpty(errors)
    }
}
