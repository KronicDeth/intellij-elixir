package org.elixir_lang.find_usages

import com.intellij.lang.findUsages.LanguageFindUsages
import org.elixir_lang.ElixirLanguage
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.psi.CallDefinitionClause
import org.elixir_lang.psi.call.Call

class ProviderTest : PlatformTestCase() {
    override fun getTestDataPath(): String = "testData/org/elixir_lang/model/psi/function"

    fun testCannotFindUsagesForFunctionDefinitionHead() {
        myFixture.configureByFiles("ctrl_click_def.ex")
        val element = myFixture.file.findElementAt(myFixture.caretOffset)
        assertNotNull("Element at caret should exist", element)

        val headCall = generateSequence(element!!) { it.parent }
            .filterIsInstance<Call>()
            .first { CallDefinitionClause.isHead(it) }

        assertFalse(
            "Function declaration heads should be handled only by Symbol API, not legacy Find Usages provider",
            LanguageFindUsages.INSTANCE.forLanguage(ElixirLanguage).canFindUsagesFor(headCall)
        )
    }

    fun testCanFindUsagesForCallSite() {
        myFixture.configureByFiles("goto_declaration.ex")
        val element = myFixture.file.findElementAt(myFixture.caretOffset)
        assertNotNull("Element at caret should exist", element)

        val callSite = generateSequence(element!!) { it.parent }
            .filterIsInstance<Call>()
            .first { !CallDefinitionClause.`is`(it) }

        assertTrue(
            "Call sites should remain eligible for legacy Find Usages provider routing where needed",
            LanguageFindUsages.INSTANCE.forLanguage(ElixirLanguage).canFindUsagesFor(callSite)
        )
    }
}
