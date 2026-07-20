package org.elixir_lang.model.psi.module_attribute

import org.elixir_lang.PlatformTestCase
import org.elixir_lang.code_insight.assertGotoDeclarationChosenAtCaret
import org.elixir_lang.code_insight.assertNoNavigationAtCaret
import org.elixir_lang.code_insight.assertShowUsagesChosenAtCaret
import org.elixir_lang.code_insight.nonDeclarationUsageCountAtCaret
import org.elixir_lang.code_insight.searchTargetCountAtCaret

class ModuleAttributeFindUsagesTest : PlatformTestCase() {
    override fun getTestDataPath(): String = "testData/org/elixir_lang/model/psi/module_attribute"

    fun testCtrlClickOnDeclarationChoosesShowUsages() {
        myFixture.configureByFiles("usages_module_attribute_declaration.ex")
        myFixture.assertShowUsagesChosenAtCaret()
        assertEquals(1, myFixture.searchTargetCountAtCaret())
    }

    fun testFindUsagesOnDeclarationFindsWritesAndReads() {
        assertEquals(2, nonDeclarationUsageCount("usages_module_attribute_declaration.ex"))
    }

    fun testCtrlClickOnUsageChoosesGotoDeclaration() {
        myFixture.configureByFiles("usages_module_attribute_usage.ex")
        myFixture.assertGotoDeclarationChosenAtCaret()
    }

    fun testFindUsagesFromUsageFindsDeclarationWrite() {
        assertEquals(1, nonDeclarationUsageCount("usages_module_attribute_usage.ex"))
    }

    fun testFindUsagesIgnoresSameNameInOtherModule() {
        assertEquals(1, nonDeclarationUsageCount("usages_module_attribute_cross_module.ex"))
    }

    fun testCtrlClickOnDocDeclarationDoesNothing() {
        myFixture.configureByFiles("goto_declaration_nonreferencing_doc.ex")
        myFixture.assertNoNavigationAtCaret()
    }

    private fun nonDeclarationUsageCount(vararg files: String): Int {
        myFixture.configureByFiles(*files)
        return myFixture.nonDeclarationUsageCountAtCaret(project)
    }
}
