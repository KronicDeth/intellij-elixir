package org.elixir_lang.model.psi.variable

import com.intellij.ide.impl.HeadlessDataManager
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.code_insight.assertGotoDeclarationChosenAtCaret
import org.elixir_lang.code_insight.assertShowUsagesChosenAtCaret
import org.elixir_lang.code_insight.gotoDeclarationDestinationAtCaret
import org.elixir_lang.code_insight.nonDeclarationUsageCountAtCaret
import org.elixir_lang.code_insight.searchTargetCountAtCaret

class VariableFindUsagesTest : PlatformTestCase() {
    override fun getTestDataPath(): String = "testData/org/elixir_lang/model/psi/variable"

    override fun setUp() {
        super.setUp()
        HeadlessDataManager.fallbackToProductionDataManager(myFixture.testRootDisposable)
    }

    fun testCtrlClickOnParameterDeclarationChoosesShowUsages() {
        myFixture.configureByFiles("usages_parameter_declaration.ex")
        myFixture.assertShowUsagesChosenAtCaret()
        assertEquals(1, myFixture.searchTargetCountAtCaret())
    }

    fun testFindUsagesOnParameterDeclarationFindsBodyRead() {
        assertEquals(1, nonDeclarationUsageCount("usages_parameter_declaration.ex"))
    }

    fun testCtrlClickOnVariableDeclarationChoosesShowUsages() {
        myFixture.configureByFiles("usages_variable_declaration.ex")
        myFixture.assertShowUsagesChosenAtCaret()
        assertEquals(1, myFixture.searchTargetCountAtCaret())
    }

    fun testFindUsagesOnVariableDeclarationFindsBodyRead() {
        assertEquals(1, nonDeclarationUsageCount("usages_variable_declaration.ex"))
    }

    fun testFindUsagesOnVariableDeclarationIgnoresSameNameInOtherModule() {
        assertEquals(1, nonDeclarationUsageCount("usages_variable_same_name_other_module.ex"))
    }

    fun testFindUsagesOnVariableDeclarationIncludesRebindingAndPinReads() {
        assertEquals(5, nonDeclarationUsageCount("usages_variable_rebinding_and_pin.ex"))
    }

    fun testFindUsagesOnRebindingDeclarationCoversWholeChain() {
        // A rebinding chain (`value = parameter; ^value = value; value = value + 1; value`) is one
        // user-facing variable, so Find Usages from ANY of its bindings covers the whole chain:
        // the first binding (write), the pin read, both same-line rhs reads, and the final read.
        // (Previously the same-line rhs read and everything before the caret's binding were
        // excluded - renaming from a later binding then corrupted the chain.)
        assertEquals(5, nonDeclarationUsageCount("usages_variable_rebinding_declaration_same_line_rhs.ex"))
    }

    fun testFindUsagesOnParameterDeclarationIncludesPinRead() {
        assertEquals(2, nonDeclarationUsageCount("usages_parameter_pin_read.ex"))
    }

    fun testFindUsagesOnParameterDeclarationIgnoresSiblingClauseSameName() {
        assertEquals(1, nonDeclarationUsageCount("usages_parameter_sibling_clause_same_name.ex"))
    }

    fun testCtrlClickOnIgnoredParameterDeclarationChoosesShowUsages() {
        myFixture.configureByFiles("usages_ignored_parameter_declaration.ex")
        myFixture.assertShowUsagesChosenAtCaret()
        assertEquals(1, myFixture.searchTargetCountAtCaret())
    }

    fun testCtrlClickOnIgnoredVariableDeclarationChoosesShowUsages() {
        myFixture.configureByFiles("usages_ignored_variable_declaration.ex")
        myFixture.assertShowUsagesChosenAtCaret()
        assertEquals(1, myFixture.searchTargetCountAtCaret())
    }

    fun testCtrlClickOnVariableUsageChoosesGotoDeclaration() {
        assertCtrlClickChoosesGotoDeclaration("goto_declaration_variable_usage.ex")
    }

    fun testCtrlClickOnParameterUsageChoosesGotoDeclaration() {
        assertCtrlClickChoosesGotoDeclaration("goto_declaration_parameter_usage.ex")
    }

    fun testGoToDeclarationFromVariableUsageNavigatesToDeclaration() {
        myFixture.configureByFiles("goto_declaration_variable_usage.ex")
        val target = myFixture.gotoDeclarationDestinationAtCaret()
        assertNotNull("Go To Declaration should navigate to variable declaration", target)
        assertEquals("variable", target!!.text)
    }

    fun testCtrlClickOnPinnedMatchRhsVariableChoosesGotoDeclaration() {
        assertCtrlClickChoosesGotoDeclaration("goto_declaration_pin_match_rhs.ex")
    }

    fun testCtrlClickOnPinnedMatchLhsVariableChoosesGotoDeclaration() {
        assertCtrlClickChoosesGotoDeclaration("goto_declaration_pin_match_lhs.ex")
    }

    fun testCtrlClickOnPinnedMatchRhsVariableInMapPatternChoosesGotoDeclaration() {
        assertCtrlClickChoosesGotoDeclaration("goto_declaration_pin_match_rhs_map.ex")
    }

    fun testCtrlClickOnPinnedMatchRhsVariableInListPatternChoosesGotoDeclaration() {
        assertCtrlClickChoosesGotoDeclaration("goto_declaration_pin_match_rhs_list.ex")
    }

    fun testCtrlClickOnPinnedMatchRhsVariableInSingleElementListPatternChoosesGotoDeclaration() {
        assertCtrlClickChoosesGotoDeclaration("goto_declaration_pin_match_rhs_list_many.ex")
    }

    fun testCtrlClickOnPinnedMatchLhsVariableInMapPatternChoosesGotoDeclaration() {
        assertCtrlClickChoosesGotoDeclaration("goto_declaration_pin_match_lhs_map.ex")
    }

    fun testCtrlClickOnPinnedMatchLhsVariableInListPatternChoosesGotoDeclaration() {
        assertCtrlClickChoosesGotoDeclaration("goto_declaration_pin_match_lhs_list.ex")
    }

    fun testCtrlClickOnPinnedMatchLhsVariableInListPatternWithSiblingChoosesGotoDeclaration() {
        assertCtrlClickChoosesGotoDeclaration("goto_declaration_pin_match_lhs_list_many.ex")
    }

    fun testCtrlClickOnPinnedMatchRhsVariableInTuplePatternChoosesGotoDeclaration() {
        assertCtrlClickChoosesGotoDeclaration("goto_declaration_pin_match_rhs_tuple.ex")
    }

    fun testCtrlClickOnPinnedMatchLhsVariableInTuplePatternChoosesGotoDeclaration() {
        assertCtrlClickChoosesGotoDeclaration("goto_declaration_pin_match_lhs_tuple.ex")
    }

    fun testCtrlClickOnPinnedMatchRhsVariableInListHeadTailPatternChoosesGotoDeclaration() {
        assertCtrlClickChoosesGotoDeclaration("goto_declaration_pin_match_rhs_list_head_tail.ex")
    }

    fun testCtrlClickOnPinnedMatchLhsVariableInListHeadTailPatternChoosesGotoDeclaration() {
        assertCtrlClickChoosesGotoDeclaration("goto_declaration_pin_match_lhs_list_head_tail.ex")
    }

    private fun nonDeclarationUsageCount(vararg files: String): Int {
        myFixture.configureByFiles(*files)
        return myFixture.nonDeclarationUsageCountAtCaret(project)
    }

    private fun assertCtrlClickChoosesGotoDeclaration(fileName: String) {
        myFixture.configureByFiles(fileName)
        myFixture.assertGotoDeclarationChosenAtCaret()
    }
}
