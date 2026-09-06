package org.elixir_lang.reference.callable

import com.intellij.psi.search.LocalSearchScope
import com.intellij.psi.util.PsiTreeUtil
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.psi.UnqualifiedNoArgumentsCall
import org.elixir_lang.reference.Callable

/**
 * A match inside bracket access, `m[x = 1]`, binds `x` for the code after it, as a match does anywhere in an
 * expression, so the use scope walk carries through the brackets to the statement that holds them.
 */
class VariableUseScopeThroughBracketArgumentsTest : PlatformTestCase() {
    fun testVariableBoundInsideBracketAccessHasAUseScope() = assertScopedToStatement("y = m[<caret>x = 1]\n")

    fun testVariableBoundInsideModuleAttributeAccessHasAUseScope() =
        assertScopedToStatement("y = @attr[<caret>x = 1]\n")

    private fun assertScopedToStatement(text: String) {
        myFixture.configureByText("brackets.ex", text)
        val variable = PsiTreeUtil.getParentOfType(
            myFixture.file.findElementAt(myFixture.caretOffset),
            UnqualifiedNoArgumentsCall::class.java
        )
        assertNotNull("caret is not on a variable", variable)
        assertTrue("x is not a variable", Callable.isVariable(variable!!))

        val (useScope, errors) = captureLoggedErrors { Callable.variableUseScope(variable) }

        assertEmpty(errors)
        assertEquals(listOf(text.replace("<caret>", "").trim()), (useScope as LocalSearchScope).scope.map { it.text })
    }
}
