package org.elixir_lang.reference.callable

import org.elixir_lang.PlatformTestCase
import org.elixir_lang.psi.call.Call
import org.elixir_lang.reference.Callable.Companion.isVariable

class Issue456Test : PlatformTestCase() {
    fun testIsVariable() {
        myFixture.configureByFiles("is_variable.ex")
        val callable = myFixture
                .file
                .findElementAt(myFixture.caretOffset)!!
                .prevSibling
        assertInstanceOf(callable, Call::class.java)
        assertFalse("order_by is incorrectly marked as a variable", isVariable(callable))
    }

    override fun getTestDataPath(): String = "testData/org/elixir_lang/reference/callable/issue_456"
}
