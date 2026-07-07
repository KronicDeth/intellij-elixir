package org.elixir_lang.model.psi.protocol

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
 * Behavior-level tests for forward Find/Show Usages: invoking Find Usages on a `defprotocol def` lists the
 * **call sites** that dispatch to it (`Protocol.function(args)`). Implementations are deliberately excluded -
 * they are reached via "Go To Implementation" (`Ctrl+Alt+B`) / the gutter marker, mirroring how Java surfaces
 * overriding methods.
 *
 * These drive the SAME production search pipeline the Find Usages action uses - the `SearchTarget`
 * resolved at the caret (via [searchTargets], i.e. our `ProtocolFunction` symbol) fed through [buildQuery]
 * (which runs `ElixirSymbolUsageSearcher` and yields the very [PsiUsage]s the tool window would display). We deliberately do NOT go through
 * `CodeInsightTestFixture.testFindUsagesUsingAction`: that wrapper fires the async, EDT-/modality-bound
 * `FindUsagesAction` and then polls for a `UsageView`, which is unreliable headless (the view is never
 * surfaced even though target arbitration is correct - verified separately: exactly one `SEARCH_TARGET`
 * = `perform/1`, no ambiguity popup). Asserting on the query's usages is deterministic and still
 * behavior-level: real caret → real symbol → real searcher → real usages. Assertions never touch
 * internal resolver classes, so they survive refactoring.
 */
class ProtocolFunctionFindUsagesTest : PlatformTestCase() {
    override fun getTestDataPath(): String = "testData/org/elixir_lang/model/psi/protocol"

    fun testProtocolCallSiteIsFound() {
        assertTrue(
            "Expected the qualified call site `Protocol.perform(value)` among the protocol function's usages",
            callSiteUsageCount("usages_use_injected.ex") >= 1
        )
    }

    fun testLiteralProtocolCallSiteIsFound() {
        assertTrue(
            "Expected the qualified call site among the protocol function's usages (literal defimpl)",
            callSiteUsageCount("usages_literal_protocol.ex") >= 1
        )
    }

    /**
     * Ctrl-Click decision (the original thrust): on a protocol function definition - a declaration/`SearchTarget` - the
     * "Go To Declaration or Usages" handler that Ctrl-Click uses
     * (`GotoDeclarationAction implements CtrlMouseAction` → `GotoDeclarationOrUsageHandler2`) chooses
     * **Show Usages** rather than doing nothing or navigating to itself.
     */
    fun testCtrlClickOnProtocolFunctionChoosesShowUsages() {
        myFixture.configureByFiles("usages_use_injected.ex")
        assertEquals(
            GotoDeclarationOrUsageHandler2.GTDUOutcome.SU,
            GotoDeclarationOrUsageHandler2.testGTDUOutcomeInNonBlockingReadAction(
                myFixture.editor, myFixture.file, myFixture.caretOffset
            )
        )
    }

    fun testCallToDifferentModuleIsNotListed() {
        assertEquals(
            "A same-named call qualified by a different module must not be a protocol function usage",
            0,
            callSiteUsageCount("usages_non_implementing.ex")
        )
    }

    /**
     * Runs Find Usages on the `defprotocol def` at the caret in [files] and counts non-declaration usages
     * located on a **call site** - the function name of a `Call` that is not a call-definition clause
     * (`def`/`defmacro`). The protocol function's own self-declaration usage is excluded via
     * [PsiUsage.declaration]. We resolve each usage by its `(file, range)` - the searcher models a
     * [PsiUsage] as file + absolute range (so its `.element` is the file, not the call name), matching how
     * the tool window navigates.
     */
    @Suppress("UnstableApiUsage")
    private fun callSiteUsageCount(vararg files: String): Int {
        myFixture.configureByFiles(*files)
        val file = myFixture.file
        val offset = myFixture.caretOffset
        val allOptions = AllSearchOptions(
            UsageOptions.createOptions(GlobalSearchScope.allScope(project)),
            textSearch = false
        )
        // Run on a background thread under a read action: the symbol search executes a name-anchored
        // word/index search (`SearchService.searchWord`), which the platform runs off the EDT - driving
        // it there mirrors the real Find Usages action and, unlike a direct EDT `findAll()`, actually
        // visits the indexed files.
        return ApplicationManager.getApplication().executeOnPooledThread(Callable<Int> {
            ReadAction.nonBlocking(Callable<Int> {
                val targets = searchTargets(file, offset)
                if (targets.isEmpty()) {
                    0
                } else {
                    val target = targets.single()
                    buildQuery(project, target, allOptions).findAll()
                        .filterIsInstance<PsiUsage>()
                        .filterNot { it.declaration }
                        .count { usage ->
                            val element = usage.file.findElementAt(usage.range.startOffset)
                            val enclosingCall = element?.let {
                                generateSequence(it) { e -> e.parent }
                                    .filterIsInstance<Call>()
                                    .firstOrNull()
                            }
                            enclosingCall != null && !CallDefinitionClause.`is`(enclosingCall)
                        }
                }
            }).executeSynchronously()
        }).get()
    }
}
