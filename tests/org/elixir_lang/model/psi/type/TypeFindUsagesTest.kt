package org.elixir_lang.model.psi.type

import org.elixir_lang.PlatformTestCase
import org.elixir_lang.code_insight.assertShowUsagesChosenAtCaret
import org.elixir_lang.code_insight.nonDeclarationUsageCountAtCaret

class TypeFindUsagesTest : PlatformTestCase() {
    override fun getTestDataPath(): String = "testData/org/elixir_lang/model/psi/type"

    fun testCtrlClickOnTypeDeclarationChoosesShowUsages() {
        myFixture.configureByFiles("usages_basic.ex")
        myFixture.assertShowUsagesChosenAtCaret()
    }

    fun testFindUsagesOnTypeDeclarationFindsSpecSites() {
        assertEquals(2, nonDeclarationUsageCount("usages_basic.ex"))
    }

    fun testFindUsagesIgnoresSameNamedTypeInAnotherModule() {
        assertEquals(1, nonDeclarationUsageCount("usages_cross_module.ex"))
    }

    fun testFindUsagesDoesNotTreatFunctionHeadVariableAsTypeUsage() {
        assertEquals(0, nonDeclarationUsageCount("usages_variable_name_not_type_usage.ex"))
    }

    private fun nonDeclarationUsageCount(vararg files: String): Int {
        myFixture.configureByFiles(*files)
        return myFixture.nonDeclarationUsageCountAtCaret(project)
    }
}
