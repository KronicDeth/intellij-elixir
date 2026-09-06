package org.elixir_lang.reference.callable

import com.intellij.psi.search.LocalSearchScope
import com.intellij.psi.util.PsiTreeUtil
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.psi.UnqualifiedNoArgumentsCall
import org.elixir_lang.reference.Callable

/**
 * Code being typed is incomplete, and the walks must not lose a variable while it is. In this grammar a parse error
 * is a zero-length marker beside the shapes the parser recovered, never an ancestor: an unclosed call keeps its
 * arguments, an unclosed `def` head keeps its parentheses. These pins are on those shapes, so a change in how the
 * parser recovers shows up here.
 */
class VariableWhileTypingTest : PlatformTestCase() {
    fun testABindingInsideAnUnclosedCallIsAVariableWithAScope() {
        myFixture.configureByText("typing.ex", "y = foo(<caret>x = 1,\n")
        val declaration = variableAtCaret()

        val (isVariable, errors) = captureLoggedErrors { Callable.isVariable(declaration) }

        assertEmpty(errors)
        assertTrue("x bound inside an unclosed call is not a variable", isVariable)
        assertEquals(listOf("y = foo(x = 1,"), (Callable.variableUseScope(declaration) as LocalSearchScope).scope.map { it.text })
    }

    fun testAGeneratorTypedAtTopLevelReportsNothing() {
        myFixture.configureByText("typing.exs", "<caret>x <- list\n")
        val binding = variableAtCaret()

        val (_, errors) = captureLoggedErrors { binding.useScope }

        assertEmpty(errors)
    }

    fun testAParameterInsideAnUnclosedHeadIsStillAParameter() {
        myFixture.configureByText("typing.ex", "def foo(<caret>x\n")
        val parameter = variableAtCaret()

        val (isParameter, errors) = captureLoggedErrors { Callable.isParameter(parameter) }

        assertEmpty(errors)
        assertTrue("x in an unclosed head is not a parameter", isParameter)
    }

    private fun variableAtCaret(): UnqualifiedNoArgumentsCall<*> = PsiTreeUtil.getParentOfType(
        myFixture.file.findElementAt(myFixture.caretOffset),
        UnqualifiedNoArgumentsCall::class.java
    )!!
}
