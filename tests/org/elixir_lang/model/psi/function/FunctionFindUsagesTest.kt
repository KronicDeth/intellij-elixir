package org.elixir_lang.model.psi.function

import com.intellij.find.usages.api.PsiUsage
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.code_insight.assertShowUsagesChosenAtCaret
import org.elixir_lang.code_insight.singleTargetPsiUsagesAtCaret
import org.elixir_lang.psi.CallDefinitionClause
import org.elixir_lang.psi.call.Call

/**
 * Behavior-level tests for forward Find/Show Usages: invoking Find Usages on a `def` lists the
 * **call sites** that dispatch to it. Mirrors [org.elixir_lang.model.psi.protocol.ProtocolFunctionFindUsagesTest] but for regular
 * module functions rather than protocol functions.
 */
class FunctionFindUsagesTest : PlatformTestCase() {
    override fun getTestDataPath(): String = "testData/org/elixir_lang/model/psi/function"

    fun testSameModuleUnqualifiedCallSiteIsFound() {
        assertTrue(
            "Expected the unqualified same-module call site `perform(value)` among the function's usages",
            callSiteUsageCount("usages_same_module.ex") >= 1
        )
    }

    fun testQualifiedCallSiteIsFound() {
        assertTrue(
            "Expected the qualified call site `UsagesQualified.perform(value)` among the function's usages",
            callSiteUsageCount("usages_qualified.ex") >= 1
        )
    }

    fun testApplyCallSiteIsFound() {
        assertTrue(
            "Expected the `apply(ApplyTarget, :reverse, [...])` call site among the function's usages",
            callSiteUsageCount("usages_apply.ex") >= 1
        )
    }

    @Suppress("UnstableApiUsage")
    fun testApplyCallSiteIsCountedOnce() {
        val usages = psiUsages("usages_apply.ex")
            .filterNot { it.declaration }
            .filter { it.file.name == "usages_apply.ex" }
            .filter { usage ->
                val line = usage.file.text.substring(0, usage.range.startOffset).count { it == '\n' } + 1
                line == 7
            }

        assertEquals(
            "Expected exactly one usage on the apply MFA atom line",
            1,
            usages.size
        )
    }

    fun testErlangQualifiedCallSiteIsNotCountedYet() {
        assertEquals(
            "The Erlang-style qualified call site `:erlang.sqrt(value)` is still an uncovered gap",
            0,
            callSiteUsageCount("usages_erlang_qualified.ex")
        )
    }

    fun testRecursiveFunctionIncludesFamilyDeclarationsAndCallSite() {
        val (totalCount, callSiteCount) = functionUsageCounts("recursive_family.ex")
        assertEquals(3, totalCount)
        assertEquals(1, callSiteCount)
    }

    @Suppress("UnstableApiUsage")
    fun testSpecUsageIsLabeledAsSpecificationNotFunctionCall() {
        val usages = psiUsages("usages_specification.ex")
            .filterNot { it.declaration }

        val specificationUsages = usages.filter { it.usageType?.toString() == "Specification" }
        assertTrue("Expected at least one @spec usage grouped as Specification", specificationUsages.isNotEmpty())
        assertTrue(
            "Expected a regular executable call site usage as well",
            usages.any { it.usageType?.toString() == "Function call" }
        )
    }

    /**
     * Ctrl-Click on a function definition - a declaration / SearchTarget - the
     * "Go To Declaration or Usages" handler (`GotoDeclarationOrUsageHandler2`) should choose
     * **Show Usages** rather than doing nothing or navigating to itself.
     */
    fun testCtrlClickOnFunctionDefChoosesShowUsages() {
        myFixture.configureByFiles("ctrl_click_def.ex")
        myFixture.assertShowUsagesChosenAtCaret()
    }

    /**
     * Runs on the same user-facing "Declaration or Usages" path (asserted via GTDUOutcome),
     * then reads usages from the resolved SearchTarget query.
     */
    private fun callSiteUsageCount(vararg files: String): Int {
        return functionUsageCounts(*files).second
    }

    private fun functionUsageCounts(vararg files: String): Pair<Int, Int> {
        val usages = psiUsages(*files)
        return usages.size to callSiteCount(usages)
    }

    @Suppress("UnstableApiUsage")
    private fun psiUsages(vararg files: String): List<PsiUsage> {
        myFixture.configureByFiles(*files)
        myFixture.assertShowUsagesChosenAtCaret()
        return myFixture.singleTargetPsiUsagesAtCaret(project)
    }

    @Suppress("UnstableApiUsage")
    private fun callSiteCount(usages: List<PsiUsage>): Int {
        return usages.count { usage ->
            if (usage.declaration) return@count false
            val element = usage.file.findElementAt(usage.range.startOffset) ?: return@count false
            val enclosingCall = generateSequence(element) { e -> e.parent }
                .filterIsInstance<Call>()
                .firstOrNull()
            enclosingCall != null &&
                !CallDefinitionClause.`is`(enclosingCall) &&
                !CallDefinitionClause.isHead(enclosingCall)
        }
    }
}
