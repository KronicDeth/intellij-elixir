package org.elixir_lang.reference.callable

import com.intellij.openapi.application.runReadAction
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.PsiSearchScopeUtil
import com.intellij.psi.util.PsiTreeUtil
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.psi.UnqualifiedNoArgumentsCall
import org.elixir_lang.psi.impl.declarations.UseScopeImpl
import org.elixir_lang.reference.Callable

/**
 * A binding the resolver finds inside a query macro is not a variable to the variable walk, so its own use scope is
 * its module's. A use reading it must still be scoped to hold itself and its own walk, whatever that module scope is.
 */
class VariableUseScopeWithAModuleScopedDeclarationTest : PlatformTestCase() {
    override fun setUp() {
        super.setUp()
        myFixture.addFileToProject(
            "lib/ecto/query.ex",
            "defmodule Ecto.Query do\n  defmacro from(expr, kw \\ []), do: nil\nend\n"
        )
    }

    fun testAUseOfAQueryBindingIsScopedToHoldItselfAndItsWalk() {
        myFixture.configureByText("query.ex", "import Ecto.Query\nfrom(p in Post, select: <caret>p)\n")
        val use = PsiTreeUtil.getParentOfType(
            myFixture.file.findElementAt(myFixture.caretOffset),
            UnqualifiedNoArgumentsCall::class.java
        )!!
        val declaration = PsiTreeUtil.findChildrenOfType(myFixture.file, UnqualifiedNoArgumentsCall::class.java)
            .first { it.text == "p" && it !== use }
        assertTrue("the query binding is not module-scoped", UseScopeImpl.get(declaration) is GlobalSearchScope)

        val scope = runReadAction { Callable.variableUseScope(use) }

        assertTrue("the use is outside its own scope", PsiSearchScopeUtil.isInScope(scope, use))
        assertTrue("the use's own walk is not kept", scope.contains(myFixture.file.virtualFile))
    }
}
