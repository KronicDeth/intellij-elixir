package org.elixir_lang.reference.callable

import org.elixir_lang.PlatformTestCase
import org.elixir_lang.psi.UnqualifiedNoArgumentsCall
import org.elixir_lang.reference.Callable.Companion.isParameter

class Issue431Test : PlatformTestCase() {
    fun testIsParameter() {
        myFixture.configureByFiles("planet.ex")
        val parameter = myFixture
                .file
                .findElementAt(myFixture.caretOffset)!!
                .prevSibling
        assertInstanceOf(parameter, UnqualifiedNoArgumentsCall::class.java)
        assertTrue("planet is not marked as a parameter", isParameter(parameter))
    }

    override fun getTestDataPath(): String = "testData/org/elixir_lang/reference/callable/issue_431"
}
