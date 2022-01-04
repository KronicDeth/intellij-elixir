package org.elixir_lang.reference.callable

import org.elixir_lang.reference.Callable.Companion.isVariable
import org.elixir_lang.reference.Callable.Companion.isParameter
import org.elixir_lang.psi.UnqualifiedNoArgumentsCall
import org.elixir_lang.PlatformTestCase

class Issue436Test : PlatformTestCase() {
    fun testIsParameter() {
        myFixture.configureByFiles("is_parameter.ex")
        val variable = myFixture
                .file
                .findElementAt(myFixture.caretOffset)!!
                .prevSibling
                .lastChild
                .lastChild
        assertInstanceOf(variable, UnqualifiedNoArgumentsCall::class.java)
        assertFalse("alias is marked as a parameter", isParameter(variable))
        assertFalse("alias is marked as a variable", isVariable(variable))
    }

    /*
     * Protected Instance Methods
     */
    override fun getTestDataPath(): String = "testData/org/elixir_lang/reference/callable/issue_436"
}
