package org.elixir_lang.reference.callable

import com.intellij.psi.search.LocalSearchScope
import com.intellij.psi.util.PsiTreeUtil
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.psi.ElixirStabOperation
import org.elixir_lang.psi.UnqualifiedNoArgumentsCall
import org.elixir_lang.reference.Callable.Companion.variableUseScope

/**
 * A variable used inside map update arguments once reported an error from the use scope walk. It is scoped like any
 * other use in the same `case` clause.
 */
class Issue517Test : PlatformTestCase() {
    fun testVariableUseScope() {
        myFixture.configureByFiles("variable_use_scope.ex")
        val callable = myFixture
                .file
                .findElementAt(myFixture.caretOffset)!!
                .parent
                .parent
        assertInstanceOf(callable, UnqualifiedNoArgumentsCall::class.java)
        val clause = PsiTreeUtil.getParentOfType(callable, ElixirStabOperation::class.java)!!

        val (useScope, errors) = captureLoggedErrors { variableUseScope(callable as UnqualifiedNoArgumentsCall<*>) }

        assertEmpty(errors)
        assertEquals(LocalSearchScope(clause), useScope)
    }

    override fun getTestDataPath(): String = "testData/org/elixir_lang/reference/callable/issue_517"
}
