package org.elixir_lang.reference.callable

import com.intellij.psi.search.LocalSearchScope
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.psi.call.Call

class Issue429Test : PlatformTestCase() {
    fun testUseScope() {
        myFixture.configureByFiles("get_use_scope.ex")
        val callable = myFixture
                .file
                .findElementAt(myFixture.caretOffset)!!
                .prevSibling
                .lastChild
                .lastChild
                .lastChild
                .lastChild
                .lastChild
        assertInstanceOf(callable, Call::class.java)

        val useScope = callable.useScope
        assertInstanceOf(useScope, LocalSearchScope::class.java)
        val localSearchScope = useScope as LocalSearchScope

        val scope = localSearchScope.scope
        assertEquals(1, scope.size)

        val singleScope = scope[0]
        assertTrue(
                "Use Scope is not the surrounding if",
                singleScope.text.startsWith("if auth == ")
        )
    }

    override fun getTestDataPath(): String = "testData/org/elixir_lang/reference/callable/issue_429"
}
