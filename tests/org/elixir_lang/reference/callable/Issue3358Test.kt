package org.elixir_lang.reference.callable

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
        /* `<-` outside a comprehension is a syntax error, but as a bare top-level statement it is scoped from its
           statement like any other; the error report this issue was about is gone either way */
        assertSame(myFixture.file, callable.parent.parent)
        assertEquals(
            listOf("room <- level_id"),
            variableUseScope(callable as UnqualifiedNoArgumentsCall<*>).scope.map { it.text }
        )
    }

    override fun getTestDataPath(): String = "testData/org/elixir_lang/reference/callable/issue_3358"
}
