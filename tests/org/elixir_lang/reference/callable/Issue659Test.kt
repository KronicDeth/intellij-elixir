package org.elixir_lang.reference.callable

import org.elixir_lang.PlatformTestCase
import org.elixir_lang.psi.call.Call
import org.elixir_lang.reference.Callable.Companion.isParameter
import org.elixir_lang.reference.Callable.Companion.isVariable

class Issue659Test : PlatformTestCase() {
    fun testIs() {
        myFixture.configureByFiles("is.ex")
        val callable = myFixture
                .file
                .findElementAt(myFixture.caretOffset)!!
                .prevSibling
        assertInstanceOf(callable, Call::class.java)
        assertFalse(
                "unresolvable no argument call in at bracket operation is incorrectly marked as a parameter",
                isParameter(callable)
        )
        assertFalse(
                "unresolvable no argument call in at bracket operation is incorrectly marked as a variable",
                isVariable(callable)
        )
    }

    override fun getTestDataPath(): String = "testData/org/elixir_lang/reference/callable/issue_659"
}
