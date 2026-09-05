package org.elixir_lang.reference.callable

import com.intellij.psi.search.LocalSearchScope
import com.intellij.psi.util.PsiTreeUtil
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.psi.UnqualifiedNoArgumentsCall
import org.elixir_lang.reference.Callable

/**
 * An `fn` still missing its `->` is met while typing. `isVariable` looks through it, so the use scope walk must too,
 * or the variable resolves but Find Usages and rename find nothing.
 */
class VariableUseScopeThroughIncompleteFnTest : PlatformTestCase() {
    fun testVariableInsideFnWithoutArrowHasAUseScope() {
        myFixture.configureByText("incomplete_fn.ex", "f = fn <caret>x end\n")
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
