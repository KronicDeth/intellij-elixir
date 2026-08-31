package org.elixir_lang.reference.callable

import com.intellij.psi.search.LocalSearchScope
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.psi.UnqualifiedNoArgumentsCall
import org.elixir_lang.reference.Callable.Companion.variableUseScope

class Issue3358Test : PlatformTestCase() {
    fun testVariableUseScope() {
        myFixture.configureByFiles("variable_use_scope.ex")
        val callable = myFixture
                .file
                .findElementAt(myFixture.caretOffset)!!
                .parent
                .parent
        assertInstanceOf(callable, UnqualifiedNoArgumentsCall::class.java)
        assertEquals(
                LocalSearchScope.EMPTY,
                variableUseScope((callable as UnqualifiedNoArgumentsCall<*>))
        )
    }

    override fun getTestDataPath(): String = "testData/org/elixir_lang/reference/callable/issue_3358"
}
