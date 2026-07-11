package org.elixir_lang.model.psi.type

import com.intellij.ide.impl.HeadlessDataManager
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.code_insight.assertShowUsagesChosenAtCaret
import org.elixir_lang.code_insight.nonDeclarationUsageCountAtCaret
import org.elixir_lang.code_insight.searchTargetCountAtCaret

class TypeVariableFindUsagesTest : PlatformTestCase() {
    override fun getTestDataPath(): String = "testData/org/elixir_lang/model/psi/type"

    override fun setUp() {
        super.setUp()
        HeadlessDataManager.fallbackToProductionDataManager(myFixture.testRootDisposable)
    }

    fun testCtrlClickOnTypeHeadVariableDeclarationChoosesShowUsages() {
        myFixture.configureByFiles("usages_type_variable_head_declaration.ex")
        myFixture.assertShowUsagesChosenAtCaret()
        assertEquals(1, myFixture.searchTargetCountAtCaret())
    }

    fun testFindUsagesOnTypeHeadVariableDeclarationFindsBodyUsage() {
        assertEquals(1, nonDeclarationUsageCount("usages_type_variable_head_declaration.ex"))
    }

    fun testCtrlClickOnSpecWhenVariableDeclarationChoosesShowUsages() {
        myFixture.configureByFiles("usages_spec_type_variable_when_declaration.ex")
        myFixture.assertShowUsagesChosenAtCaret()
        assertEquals(1, myFixture.searchTargetCountAtCaret())
    }

    fun testFindUsagesOnSpecWhenVariableDeclarationFindsParameterAndReturnUsages() {
        assertEquals(2, nonDeclarationUsageCount("usages_spec_type_variable_when_declaration.ex"))
    }

    private fun nonDeclarationUsageCount(vararg files: String): Int {
        myFixture.configureByFiles(*files)
        return myFixture.nonDeclarationUsageCountAtCaret(project)
    }
}
