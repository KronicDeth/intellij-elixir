package org.elixir_lang.reference.callable

import com.intellij.openapi.application.runReadAction
import com.intellij.psi.search.PsiSearchScopeUtil
import com.intellij.psi.util.PsiTreeUtil
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.psi.UnqualifiedNoArgumentsCall
import org.elixir_lang.reference.Callable

/**
 * A use is found from its own position, so its scope must hold the use itself and the binding it reads, wherever
 * resolution and the rebinding chain lead: into a preceding branch, into an interpolation, or into another file.
 */
class VariableUseScopeHoldsTheUseTest : PlatformTestCase() {
    fun testAUseAfterARebindingThatFollowsAnIfBranch() = assertHeld(
        "defmodule M do\n  def f(c) do\n    if c do\n      x = 1\n    end\n    x = 2\n    y = <caret>x\n  end\nend\n"
    )

    fun testAUseAfterARebindingThatFollowsAnInterpolationBinding() =
        assertHeld("defmodule M do\n  def f do\n    \"#{x = 1}\"\n    x = 2\n    y = <caret>x\n  end\nend\n")

    fun testAUseResolvedThroughUseIsScopedInItsOwnFile() {
        myFixture.configureByText(
            "injector.ex",
            "defmodule Injector do\n  defmacro __using__(_opts) do\n    quote do\n      x = 1\n    end\n  end\nend\n"
        )
        myFixture.configureByText("user.ex", "defmodule User do\n  use Injector\n  y = <caret>x\nend\n")
        val use = variableAtCaret()

        val scope = runReadAction { Callable.variableUseScope(use) }

        assertTrue("the use is outside its own scope", PsiSearchScopeUtil.isInScope(scope, use))
        assertTrue("the use's file is not in its scope", myFixture.file.virtualFile in scope.virtualFiles)
    }

    /** The use reads the `x = 2` rebinding; its scope must hold both. */
    private fun assertHeld(text: String) {
        myFixture.configureByText("held.ex", text)
        val use = variableAtCaret()

        val scope = runReadAction { Callable.variableUseScope(use) }

        assertTrue("the use is outside its own scope", PsiSearchScopeUtil.isInScope(scope, use))
        val bound = PsiTreeUtil.findChildrenOfAnyType(myFixture.file, UnqualifiedNoArgumentsCall::class.java)
            .first { it.parent.text == "x = 2" }
        assertTrue("`x = 2` is outside the use's scope", PsiSearchScopeUtil.isInScope(scope, bound))
    }

    private fun variableAtCaret(): UnqualifiedNoArgumentsCall<*> = PsiTreeUtil.getParentOfType(
        myFixture.file.findElementAt(myFixture.caretOffset),
        UnqualifiedNoArgumentsCall::class.java
    )!!
}
