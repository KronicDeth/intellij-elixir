@file:Suppress("UnstableApiUsage")

package org.elixir_lang.code_insight

import com.intellij.codeInsight.completion.CompletionType
import com.intellij.codeInsight.navigation.actions.GotoDeclarationOrUsageHandler2
import com.intellij.codeInsight.navigation.actions.GotoDeclarationOrUsageHandler2.GTDUOutcome
import com.intellij.find.usages.api.PsiUsage
import com.intellij.find.usages.api.SearchTarget
import com.intellij.find.usages.api.UsageOptions
import com.intellij.find.usages.impl.AllSearchOptions
import com.intellij.find.usages.impl.buildQuery
import com.intellij.find.usages.impl.searchTargets
import com.intellij.openapi.actionSystem.IdeActions
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.testFramework.fixtures.CodeInsightTestFixture
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import java.util.concurrent.Callable

/**
 * Reusable behavioural gestures for driving IDE features the way a user would - Ctrl+Click
 * (Go To Declaration / Usage), completion popup, and Find Usages - and asserting on the
 * user-visible outcome rather than hand-resolved PSI references.
 *
 * Shared by the navigation/completion/find-usages test suites so the boilerplate for each gesture
 * lives in exactly one place.
 */

/**
 * The Ctrl+Click outcome at the caret: [GTDUOutcome.GTD] (single unambiguous declaration to jump to),
 * [GTDUOutcome.SU] (show usages), or `null` (nothing to navigate to).
 */
fun CodeInsightTestFixture.gtduOutcomeAtCaret(): GTDUOutcome? =
    GotoDeclarationOrUsageHandler2.testGTDUOutcomeInNonBlockingReadAction(editor, file, caretOffset)

/** Asserts Ctrl+Click at the caret chooses "go to declaration". */
fun CodeInsightTestFixture.assertGotoDeclarationChosenAtCaret(message: String? = null) {
    assertEquals(message ?: "Ctrl+Click should choose go-to-declaration", GTDUOutcome.GTD, gtduOutcomeAtCaret())
}

/** Asserts Ctrl+Click at the caret chooses "show usages". */
fun CodeInsightTestFixture.assertShowUsagesChosenAtCaret(message: String? = null) {
    assertEquals(message ?: "Ctrl+Click should choose show-usages", GTDUOutcome.SU, gtduOutcomeAtCaret())
}

/** Asserts Ctrl+Click at the caret does nothing (no declaration and no usages). */
fun CodeInsightTestFixture.assertNoNavigationAtCaret(message: String? = null) {
    assertNull(message ?: "Ctrl+Click should not navigate", gtduOutcomeAtCaret())
}

/**
 * Performs the real Go To Declaration action and returns the PSI element the caret lands on
 * (or `null` if it produced no destination).
 */
fun CodeInsightTestFixture.gotoDeclarationDestination(): PsiElement? {
    performEditorAction(IdeActions.ACTION_GOTO_DECLARATION)
    return file.findElementAt(caretOffset)
}

/**
 * The completion candidates offered at the caret (`null` when the popup wasn't shown - e.g. a single
 * match was auto-inserted, or nothing was offered).
 */
fun CodeInsightTestFixture.completionStringsAtCaret(): List<String>? {
    complete(CompletionType.BASIC)
    return lookupElementStrings
}

/**
 * The [PsiUsage]s the Find Usages tool window would display for the search target resolved at the
 * caret (including the declaration usage; empty when the caret resolves to no unambiguous target).
 *
 * Drives the real Find Usages pipeline - `caret → searchTargets → buildQuery → PsiUsage`. The symbol
 * search runs off the EDT under a read action because it executes a name-anchored word/index search
 * (`SearchService.searchWord`) that the platform runs on a background thread; driving it there mirrors
 * the real action and, unlike a direct EDT `findAll()`, actually visits the indexed files.
 */
fun CodeInsightTestFixture.psiUsagesAtCaret(project: Project): List<PsiUsage> =
    psiUsagesAtCaret(project) { it.singleOrNull() }

/**
 * Like [psiUsagesAtCaret] but *asserts* the caret resolves to exactly one search target (failing if
 * there are zero or several), for callers whose fixtures are constructed to have a single unambiguous
 * target.
 */
fun CodeInsightTestFixture.singleTargetPsiUsagesAtCaret(project: Project): List<PsiUsage> =
    psiUsagesAtCaret(project) { it.single() }

@Suppress("UnstableApiUsage")
private fun CodeInsightTestFixture.psiUsagesAtCaret(
    project: Project,
    selectTarget: (List<SearchTarget>) -> SearchTarget?
): List<PsiUsage> {
    val targetFile = file
    val offset = caretOffset
    val allOptions = AllSearchOptions(
        UsageOptions.createOptions(GlobalSearchScope.allScope(project)),
        textSearch = false
    )
    return ApplicationManager.getApplication().executeOnPooledThread(Callable {
        ReadAction.nonBlocking(Callable {
            val target = selectTarget(searchTargets(targetFile, offset)) ?: return@Callable emptyList<PsiUsage>()
            buildQuery(project, target, allOptions).findAll().filterIsInstance<PsiUsage>()
        }).executeSynchronously()
    }).get()
}

/** Number of Find Usages results at the caret that are *not* the declaration itself. */
fun CodeInsightTestFixture.nonDeclarationUsageCountAtCaret(project: Project): Int =
    psiUsagesAtCaret(project).filterNot { it.declaration }.size

/** Number of Find Usages search targets resolved at the caret. */
@Suppress("UnstableApiUsage")
fun CodeInsightTestFixture.searchTargetCountAtCaret(): Int = searchTargets(file, caretOffset).size
