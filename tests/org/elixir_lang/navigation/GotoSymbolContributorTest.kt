package org.elixir_lang.navigation

import com.intellij.navigation.ChooseByNameRegistry
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase
import org.elixir_lang.psi.call.Call
import org.elixir_lang.structure_view.element.CallDefinitionClause

class GotoSymbolContributorTest : LightPlatformCodeInsightFixtureTestCase() {
    private fun gotoSymbolContributor(): GotoSymbolContributor {
        val symbolModelContributors = ChooseByNameRegistry
                .getInstance()
                .symbolModelContributors

        var gotoSymbolContributor: GotoSymbolContributor? = null

        for (symbolModelContributor in symbolModelContributors) {
            if (symbolModelContributor is GotoSymbolContributor) {
                gotoSymbolContributor = symbolModelContributor
            }
        }

        assertNotNull(gotoSymbolContributor)

        return gotoSymbolContributor!!
    }

    override fun getTestDataPath(): String {
        return "testData/org/elixir_lang/navigation/goto_symbol_contributor"
    }

    /*
     * Tests
     */

    fun testIssue472() {
        myFixture.configureByFile("issue_472.ex")
        val gotoSymbolContributor = gotoSymbolContributor()
        val itemsByName = gotoSymbolContributor.getItemsByName(
                "decode_auth_type",
                "decode_a",
                myFixture.project,
                false
        )

        assertEquals(1, itemsByName.size)

        assertInstanceOf(itemsByName[0], CallDefinitionClause::class.java)
        val callDefinitionClause = itemsByName[0] as CallDefinitionClause
        assertEquals("decode_auth_type", callDefinitionClause.name)
    }

    fun testIssue705BeforeCompile() {
        myFixture.configureByFile("issue_705__before_compile__.ex")
        val elementAtCaret = myFixture.file.findElementAt(myFixture.caretOffset)

        val call = elementAtCaret!!.parent.parent.parent.parent as Call

        val enclosingModularMacroCall = CallDefinitionClause.enclosingModularMacroCall(call)
        assertNotNull(enclosingModularMacroCall)

        val modular = CallDefinitionClause.enclosingModular(call)
        assertNotNull(modular)
    }

    fun testIssue705TypeFunctions() {
        myFixture.configureByFile("issue_705_type_functions.ex")
        val elementAtCaret = myFixture.file.findElementAt(myFixture.caretOffset)

        val call = elementAtCaret!!.parent.parent.parent as Call

        val enclosingModularMacroCall = CallDefinitionClause.enclosingModularMacroCall(call)
        assertNotNull(enclosingModularMacroCall)

        val modular = CallDefinitionClause.enclosingModular(call)
        assertNotNull(modular)
    }

    fun testIssue1141() {
        myFixture.configureByFile("issue_1141.ex")
        val elementAtCaret = myFixture.file.findElementAt(myFixture.caretOffset)

        val call = elementAtCaret!!.parent.parent.parent.parent as Call

        val enclosingModularMacroCall = CallDefinitionClause.enclosingModularMacroCall(call)
        assertNotNull(enclosingModularMacroCall)

        val modular = CallDefinitionClause.enclosingModular(call)
        assertNotNull(modular)
    }

    fun testIssue1228() {
        myFixture.configureByFile("issue_1228.ex")
        val elementAtCaret = myFixture.file.findElementAt(myFixture.caretOffset)

        val call = elementAtCaret!!.parent.parent.parent.parent as Call

        val enclosingModularMacroCall = CallDefinitionClause.enclosingModularMacroCall(call)
        assertNotNull(enclosingModularMacroCall)

        val modular = CallDefinitionClause.enclosingModular(call)
        assertNotNull(modular)
    }
}
