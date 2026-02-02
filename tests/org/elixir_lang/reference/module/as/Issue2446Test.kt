package org.elixir_lang.reference.module.`as`

import com.intellij.codeInsight.completion.CompletionType
import org.elixir_lang.PlatformTestCase

class Issue2446Test : PlatformTestCase() {
    fun testCompletion() {
        myFixture.configureByFiles("index.ex", "asd_xS@ps.ex")
        myFixture.complete(CompletionType.BASIC, 1)
        val strings = myFixture.lookupElementStrings
        assertNotNull("Completion lookup shown", strings)
        assertEquals(1, strings!!.size)
        assertContainsElements(strings, listOf("foo"))
    }

    override fun getTestDataPath(): String = "testData/org/elixir_lang/reference/module/as/issue_2446"
}
