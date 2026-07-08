package org.elixir_lang.model.psi.function

import com.intellij.codeInsight.navigation.actions.GotoDeclarationOrUsageHandler2
import com.intellij.find.usages.api.PsiUsage
import com.intellij.find.usages.api.UsageOptions
import com.intellij.find.usages.impl.AllSearchOptions
import com.intellij.find.usages.impl.buildQuery
import com.intellij.find.usages.impl.searchTargets
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ReadAction
import com.intellij.psi.search.GlobalSearchScope
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.psi.CallDefinitionClause
import org.elixir_lang.psi.call.Call
import java.util.concurrent.Callable

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
        assertEquals(
            GotoDeclarationOrUsageHandler2.GTDUOutcome.SU,
            GotoDeclarationOrUsageHandler2.testGTDUOutcomeInNonBlockingReadAction(
                myFixture.editor, myFixture.file, myFixture.caretOffset
            )
        )
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
        assertEquals(
            GotoDeclarationOrUsageHandler2.GTDUOutcome.SU,
            GotoDeclarationOrUsageHandler2.testGTDUOutcomeInNonBlockingReadAction(
                myFixture.editor, myFixture.file, myFixture.caretOffset
            )
        )

        val file = myFixture.file
        val offset = myFixture.caretOffset
        val allOptions = AllSearchOptions(
            UsageOptions.createOptions(GlobalSearchScope.allScope(project)),
            textSearch = false
        )

        return ApplicationManager.getApplication().executeOnPooledThread(Callable {
            ReadAction.nonBlocking(Callable {
                val target = searchTargets(file, offset).single()
                buildQuery(project, target, allOptions).findAll().filterIsInstance<PsiUsage>()
            }).executeSynchronously()
        }).get()
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
