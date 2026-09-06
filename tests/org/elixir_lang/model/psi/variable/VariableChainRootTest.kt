package org.elixir_lang.model.psi.variable

import com.intellij.psi.PsiElement
import com.intellij.psi.search.LocalSearchScope
import com.intellij.psi.search.PsiSearchScopeUtil
import com.intellij.psi.util.PsiTreeUtil
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.psi.UnqualifiedNoArgumentsCall

/**
 * A rebinding chains to the binding a read just before it would resolve to, so the chain root is decided by the
 * same scoping the resolver applies: a binding inside a preceding `if` branch does not leak out, a binding outside
 * a `case` clause is visible inside it, and a top-level statement binds for the statements after it.
 */
class VariableChainRootTest : PlatformTestCase() {
    fun testARebindingAfterAnIfBranchDoesNotChainIntoTheBranch() {
        val scope = maximalSearchScope(
            "defmodule M do\n  def f(c) do\n    if c do\n      x = 1\n    end\n    <caret>x = 2\n    y = x\n  end\n" +
                "end\n"
        )

        assertHolds(scope, "y = x")
    }

    fun testATopLevelRebindingChainsToTheEarlierBinding() {
        val scope = maximalSearchScope("x = 1\n<caret>x = x + 1\ny = x\n")

        assertHolds(scope, "x = 1")
    }

    fun testARebindingInAnFnDoesNotChainToTheDefParameter() {
        val definition = "def f(x) do\n    fn x -> x = x + 1 end\n  end"
        val scope = maximalSearchScope("defmodule M do\n  ${definition.replace("-> x", "-> <caret>x")}\nend\n")

        assertFalse("the def is inside the fn's scope", PsiSearchScopeUtil.isInScope(scope, element(definition)))
    }

    fun testARebindingInACaseClauseChainsToTheBindingOutsideIt() {
        val scope = maximalSearchScope(
            "defmodule M do\n  def f(c) do\n    x = 1\n    case c do\n      _ -> <caret>x = 2\n    end\n  end\nend\n"
        )

        assertHolds(scope, "x = 1")
    }

    private fun maximalSearchScope(text: String): LocalSearchScope {
        myFixture.configureByText("chain.ex", text)
        val declaration = PsiTreeUtil.getParentOfType(
            myFixture.file.findElementAt(myFixture.caretOffset),
            UnqualifiedNoArgumentsCall::class.java
        )!!
        val symbol = VariableSymbol.fromDeclaration(declaration)
        assertNotNull("caret is not on a declaration", symbol)

        return symbol!!.maximalSearchScope as LocalSearchScope
    }

    private fun assertHolds(scope: LocalSearchScope, text: String) =
        assertTrue("`$text` is outside the chain's scope", PsiSearchScopeUtil.isInScope(scope, element(text)))

    /** The outermost element whose text is exactly [text]. */
    private fun element(text: String): PsiElement =
        PsiTreeUtil.findChildrenOfAnyType(myFixture.file, PsiElement::class.java).first { it.text == text }
}
