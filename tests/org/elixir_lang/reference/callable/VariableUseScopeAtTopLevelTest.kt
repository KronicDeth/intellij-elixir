package org.elixir_lang.reference.callable

import com.intellij.psi.search.LocalSearchScope
import com.intellij.psi.util.PsiTreeUtil
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.psi.UnqualifiedNoArgumentsCall
import org.elixir_lang.reference.Callable

/**
 * A variable bound in a top-level statement is visible to every statement after it. A declaration is scoped to its
 * statement and what follows; a use takes its declaration's scope, so the scope of either holds every reference.
 */
class VariableUseScopeAtTopLevelTest : PlatformTestCase() {
    fun testBindingInsideATopLevelListIsScopedFromItsStatement() =
        assertScoped("[<caret>x = 1]\nx\n", "[x = 1]", "x")

    fun testBindingInsideATopLevelCallIsScopedFromItsStatement() =
        assertScoped("foo(<caret>x = 1)\nx\n", "foo(x = 1)", "x")

    fun testAUseAtTopLevelIsScopedFromItsDeclaration() = assertScoped("x = 1\ny = <caret>x\n", "x = 1", "y = x")

    fun testAUseWithNoDeclarationIsScopedFromItsOwnStatement() = assertScoped("y = <caret>x\n", "y = x")

    fun testADeclarationIsScopedToItsStatementAndWhatFollows() =
        assertScoped("<caret>x = 1\ny = 2\nz = x\n", "x = 1", "y = 2", "z = x")

    fun testAUseIsScopedFromItsDeclarationPastTheStatementsBetween() =
        assertScoped("x = 1\ny = 2\nz = <caret>x\n", "x = 1", "y = 2", "z = x")

    fun testAUseOfARebindingIsScopedFromTheChainRoot() =
        assertScoped("x = 1\nx = x + 1\ny = <caret>x\n", "x = 1", "x = x + 1", "y = x")

    private fun assertScoped(text: String, vararg statements: String) {
        myFixture.configureByText("script.exs", text)
        val variable = PsiTreeUtil.getParentOfType(
            myFixture.file.findElementAt(myFixture.caretOffset),
            UnqualifiedNoArgumentsCall::class.java
        )
        assertNotNull("caret is not on a variable", variable)
        assertTrue("x is not a variable", Callable.isVariable(variable!!))

        val (useScope, errors) = captureLoggedErrors { Callable.variableUseScope(variable) }

        assertEmpty(errors)
        assertEquals(statements.toList(), (useScope as LocalSearchScope).scope.map { it.text })
    }
}
