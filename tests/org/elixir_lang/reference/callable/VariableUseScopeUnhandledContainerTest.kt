package org.elixir_lang.reference.callable

import com.intellij.psi.search.LocalSearchScope
import com.intellij.psi.util.PsiTreeUtil
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.psi.UnqualifiedNoArgumentsCall
import org.elixir_lang.reference.Callable

/**
 * The use-scope walk names the containers a variable can be declared through and stops, with an
 * empty scope, at anything that cannot declare one. A container it does not name is the latter
 * case, not an error: a match inside bracket access declares nothing outside the brackets.
 */
class VariableUseScopeUnhandledContainerTest : PlatformTestCase() {
    fun testVariableInsideBracketArgumentsHasEmptyUseScope() {
        myFixture.configureByText("brackets.ex", "foo[<caret>x = 1]\n")
        val variable = PsiTreeUtil.getParentOfType(
            myFixture.file.findElementAt(myFixture.caretOffset),
            UnqualifiedNoArgumentsCall::class.java
        )
        assertNotNull("caret is not on a variable", variable)

        val (useScope, errors) = captureLoggedErrors { Callable.variableUseScope(variable!!) }

        assertEmpty("a container that declares nothing is not an error", errors)
        assertEquals(LocalSearchScope.EMPTY, useScope)
    }
}
