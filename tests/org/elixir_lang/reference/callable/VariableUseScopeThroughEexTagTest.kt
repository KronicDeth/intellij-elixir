package org.elixir_lang.reference.callable

import com.intellij.openapi.application.runReadAction
import com.intellij.psi.PsiElement
import com.intellij.psi.search.PsiSearchScopeUtil
import com.intellij.psi.util.PsiTreeUtil
import org.elixir_lang.ElixirLanguage
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.code_insight.nonDeclarationUsageCountAtCaret
import org.elixir_lang.psi.UnqualifiedNoArgumentsCall
import org.elixir_lang.reference.Callable

/**
 * A template is one expression, so a variable bound in a tag is in scope for the tags after it, to the end of the
 * block the tag stands in: a tag scopes like the statement it is, not like a block that ends at its delimiter.
 */
class VariableUseScopeThroughEexTagTest : PlatformTestCase() {
    fun testDeclarationInATopLevelTagReachesALaterTag() =
        assertScopeReachesLaterUse("<% <caret>x = 1 %>\n<p><%= x %></p>\n")

    fun testDeclarationInATagInsideABlockReachesALaterTagInTheBlock() =
        assertScopeReachesLaterUse("<%= if @ok do %><p>a</p><% <caret>x = 1 %><p><%= x %></p><% end %>\n")

    fun testUseInATagIsScopedToHoldItselfAndItsBinding() {
        val use = variableAtCaret("<% x = 1 %>\n<p><%= <caret>x %></p>\n")
        val binding = variablesNamed("x").first { it.textOffset < use.textOffset }

        val scope = runReadAction { Callable.variableUseScope(use) }

        assertTrue("the use is outside its own scope", PsiSearchScopeUtil.isInScope(scope, use))
        assertTrue("the binding is outside the use's scope", PsiSearchScopeUtil.isInScope(scope, binding))
    }

    fun testFindUsagesFromATagBindingFindsTheUseInALaterTag() {
        myFixture.configureByText(
            "block.html.eex",
            "<%= if @ok do %><p>a</p><% <caret>total = 1 %><p><%= total %></p><% end %>\n"
        )

        assertEquals("the use in the later tag was not found", 1, myFixture.nonDeclarationUsageCountAtCaret(project))
    }

    private fun assertScopeReachesLaterUse(text: String) {
        val declaration = variableAtCaret(text)
        val use = variablesNamed("x").first { it.textOffset > declaration.textOffset }

        val scope = runReadAction { Callable.variableUseScope(declaration) }

        assertTrue("the later tag's use is outside the binding's scope", PsiSearchScopeUtil.isInScope(scope, use))
    }

    private fun variableAtCaret(text: String): UnqualifiedNoArgumentsCall<*> {
        myFixture.configureByText("page.html.eex", text)

        return PsiTreeUtil.getParentOfType(
            elixirRoot().findElementAt(myFixture.caretOffset),
            UnqualifiedNoArgumentsCall::class.java
        )!!
    }

    private fun variablesNamed(name: String): List<UnqualifiedNoArgumentsCall<*>> =
        PsiTreeUtil.findChildrenOfType(elixirRoot(), UnqualifiedNoArgumentsCall::class.java).filter { it.text == name }

    private fun elixirRoot(): PsiElement =
        checkNotNull(myFixture.file.viewProvider.getPsi(ElixirLanguage)) { "no Elixir root" }
}
