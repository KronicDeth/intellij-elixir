package org.elixir_lang.reference.callable

import com.intellij.psi.search.LocalSearchScope
import com.intellij.psi.util.PsiTreeUtil
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.psi.UnqualifiedNoArgumentsCall
import org.elixir_lang.reference.Callable

/**
 * A match inside map update arguments binds its variable for the code that follows, as it does anywhere else in an
 * expression, so the use scope walk must carry through the arguments as `isVariable` does.
 */
class VariableUseScopeThroughMapUpdateTest : PlatformTestCase() {
    fun testVariableBoundInsideMapUpdateHasAUseScope() {
        myFixture.configureByText("map_update.ex", "y = %{m | k: <caret>x = 1}\n")
        val variable = PsiTreeUtil.getParentOfType(
            myFixture.file.findElementAt(myFixture.caretOffset),
            UnqualifiedNoArgumentsCall::class.java
        )
        assertNotNull("caret is not on a variable", variable)
        assertTrue("x is not a variable", Callable.isVariable(variable!!))

        val (useScope, errors) = captureLoggedErrors { Callable.variableUseScope(variable) }

        assertEmpty(errors)
        assertNotSame(LocalSearchScope.EMPTY, useScope)
    }
}
