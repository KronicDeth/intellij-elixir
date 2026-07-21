@file:Suppress("UnstableApiUsage")

package org.elixir_lang.code_insight

import com.intellij.codeInsight.completion.CompletionType
import com.intellij.find.usages.api.PsiUsage
import com.intellij.find.usages.api.SearchTarget
import com.intellij.find.usages.api.UsageOptions
import com.intellij.find.usages.impl.AllSearchOptions
import com.intellij.find.usages.impl.buildQuery
import com.intellij.find.usages.impl.searchTargets
import com.intellij.model.psi.impl.targetSymbols
import com.intellij.refactoring.rename.api.RenameTarget
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.testFramework.fixtures.CodeInsightTestFixture
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
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
 * One resolved Go-To-Declaration target: the [destination] leaf the navigation actually lands on, resolved
 * from the target's `NavigationRequest`. `null` when the request is not a plain source/PSI one (e.g. a
 * directory target), or - the bug this suite guards against - when the gesture "decided" to navigate but
 * resolved nowhere.
 */
data class GtduTarget(val destination: PsiElement?)

/**
 * The faithful outcome of the real Go-To-Declaration-or-Usages gesture at the caret, carrying the actual
 * navigation targets the platform resolved - not merely which branch it chose.
 *
 * See [gtduNavigationAtCaret] for the full rationale; in short:
 *  - [None] - the gesture resolves to nothing. Ctrl+Click / Ctrl+B does nothing, the caret stays put.
 *  - [GotoDeclaration] - the gesture navigates to one or more [GtduTarget]s (each carrying the [GtduTarget.destination]
 *    leaf the navigation lands on, so "navigated to the real declaration" is distinguishable from "navigated nowhere").
 *  - [ShowUsages] - the gesture opens Show Usages (the caret is on a declaration); `variantCount` is the
 *    number of target variants offered.
 */
sealed interface GtduNavigation {
    object None : GtduNavigation
    data class GotoDeclaration(val targets: List<GtduTarget>) : GtduNavigation
    data class ShowUsages(val variantCount: Int) : GtduNavigation
}

/**
 * Reproduces the platform's real `GotoDeclarationOrUsageHandler2` gesture faithfully and returns the
 * **actual navigation the platform resolved** (destination leaf per target, or the Show-Usages branch),
 * wrapped in [GtduNavigation].
 *
 * This is the single faithful foundation for every navigation/Find-Usages *decision* and *destination*
 * assertion in the suite.
 *
 * WHAT THE REAL HANDLER DOES (and why the harness must mirror it): `GotoDeclarationOrUsageHandler2`
 * resolves `fromGTDProviders(project, editor, offset)?.toGTDUActionData() ?: gotoDeclarationOrUsages(file, offset)`
 * - i.e. it consults the `GotoDeclarationHandler` EP providers *first*, and only falls back to the Symbol/
 * target path if no EP contributes. This ordering matters here specifically: the decompiled-`.beam` bug
 * originally aborted the gesture *inside* `fromGTDProviders`, when a third-party `GotoDeclarationHandler`
 * (JS/LESS/...) called `getNode().getElementType()` on the coarse, `getNode()==null` definition leaf and
 * threw. A harness that skipped `fromGTDProviders` would silently diverge from the real IDE. So this
 * front-runs the EP providers exactly as the handler does (an EP result always maps to go-to-declaration,
 * never show-usages - `toGTDUActionData` wraps it as `GTD`).
 *
 * WHY NOT THE PUBLIC `@TestOnly` HOOK: `testGTDUOutcomeInNonBlockingReadAction` is faithful to the handler
 * (it too front-runs the EP providers), but returns only the *branch decision* (`GTD`/`SU`/`null`). It
 * throws away the *resolved destination*, which the beam bug demands: the failure mode is "the gesture
 * decides GTD but resolves onto the coarse self-named definition, i.e. navigates nowhere". The coarse
 * `TypeDefinitionImpl`/`CallDefinitionImpl` spans the WHOLE definition, so a caret-position read after
 * `performEditorAction` cannot tell "went nowhere (self)" from "landed on the definition" - it stayed
 * green with the fix removed. Only the resolved [GtduTarget.destination] leaf distinguishes broken from
 * fixed; capturing it is why this reaches past the public hook into the internal result tree.
 *
 * WHY REFLECTION: `gotoDeclarationOrUsages` / `fromGTDProviders` and the `GTDActionData` /
 * `GTDUActionData` / `GTDUActionResult` / `NavigationActionResult` result tree (and the `NavigationRequest`
 * we resolve the destination from) are Kotlin-`internal` (and `@ApiStatus.Internal`) - deliberately hidden
 * so JetBrains can evolve them, which is exactly why only the decision enum is exposed publicly. From a
 * plugin's test module the only way to reach the resolved destination is reflectively. The public method
 * names are stable in this platform build (`result`, `getNavigationActionResult`, `getRequestor`,
 * `getTargets`, `navigationRequest`, `getTargetVariants`); `setAccessible(true)` is required because the
 * methods, though `public`, are declared on non-public (`internal`) classes.
 *
 * Runs on a pooled thread under a non-blocking read action (like [psiUsagesAtCaret]) because the real
 * action resolves off the EDT and Show-Usages variant building may touch the index.
 */
fun CodeInsightTestFixture.gtduNavigationAtCaret(): GtduNavigation {
    val targetFile = file
    val offset = caretOffset
    val project = project
    val editor = editor
    return ApplicationManager.getApplication().executeOnPooledThread(Callable {
        ReadAction.nonBlocking(Callable { resolveGtduNavigation(project, editor, targetFile, offset) }).executeSynchronously()
    }).get()
}

private fun resolveGtduNavigation(project: Project, editor: Editor, targetFile: PsiFile, offset: Int): GtduNavigation {
    // Faithful to GotoDeclarationOrUsageHandler2: fromGTDProviders(...)?.toGTDUActionData() ?: gotoDeclarationOrUsages(...).
    // An EP-provider result is always go-to-declaration (toGTDUActionData wraps its NavigationActionResult as GTD);
    // it falls through to the Symbol/target path only when no provider contributes a navigable result.
    fromGtdProviders(project, editor, offset)?.call("result")?.let { epResult ->
        return GtduNavigation.GotoDeclaration(epResult.toGtduTargets(project))
    }

    val actionData = invokeGtdu(targetFile, offset) ?: return GtduNavigation.None
    val result = actionData.call("result") ?: return GtduNavigation.None
    return when (result.javaClass.simpleName) {
        // GTDUActionResult.GTD - carries a NavigationActionResult (Single- or MultipleTargets).
        "GTD" -> {
            val navigationActionResult = result.call("getNavigationActionResult") ?: return GtduNavigation.None
            GtduNavigation.GotoDeclaration(navigationActionResult.toGtduTargets(project))
        }
        // GTDUActionResult.SU - the Show Usages branch; carries the offered target variants.
        "SU" -> GtduNavigation.ShowUsages((result.call("getTargetVariants") as List<*>).size)
        else -> GtduNavigation.None
    }
}

/** Reflectively invokes the internal `GtduKt.gotoDeclarationOrUsages(file, offset)`; returns a `GTDUActionData?`. */
private fun invokeGtdu(targetFile: PsiFile, offset: Int): Any? =
    Class.forName("com.intellij.codeInsight.navigation.impl.GtduKt")
        .getDeclaredMethod("gotoDeclarationOrUsages", PsiFile::class.java, Int::class.javaPrimitiveType)
        .apply { isAccessible = true }
        .invoke(null, targetFile, offset)

/** Reflectively invokes the internal `GtdKt.gotoDeclaration(file, offset)`; returns a `GTDActionData?`. */
private fun invokeGtd(targetFile: PsiFile, offset: Int): Any? =
    Class.forName("com.intellij.codeInsight.navigation.impl.GtdKt")
        .getDeclaredMethod("gotoDeclaration", PsiFile::class.java, Int::class.javaPrimitiveType)
        .apply { isAccessible = true }
        .invoke(null, targetFile, offset)

/**
 * Reflectively invokes the internal `GtdProvidersKt.fromGTDProviders(project, editor, offset)` - the
 * `GotoDeclarationHandler` EP-provider stage both real handlers ([GotoDeclarationOrUsageHandler2] and the
 * Ctrl+B [com.intellij.codeInsight.navigation.actions.GotoDeclarationOnlyHandler2]) run before the Symbol
 * path. Returns a `GTDActionData?` (whose `result()` is a `NavigationActionResult?`), or `null` when no EP
 * provider contributes a target at the offset (the common case for Elixir, which registers none).
 */
private fun fromGtdProviders(project: Project, editor: Editor, offset: Int): Any? =
    Class.forName("com.intellij.codeInsight.navigation.impl.GtdProvidersKt")
        .getDeclaredMethod("fromGTDProviders", Project::class.java, Editor::class.java, Int::class.javaPrimitiveType)
        .apply { isAccessible = true }
        .invoke(null, project, editor, offset)

/**
 * Reproduces the *pure* Go To Declaration gesture (Ctrl+B, `GotoDeclarationOnlyHandler2`) faithfully:
 * `fromGTDProviders(project, editor, offset) ?: gotoDeclaration(file, offset)`, then `.result()`. Returns
 * the resolved declaration targets, or `null` when there is nothing to navigate to.
 *
 * This is a DIFFERENT real gesture from [gtduNavigationAtCaret]: that one is the combined Ctrl+Click that
 * *chooses* between go-to-declaration and show-usages; this always resolves the declaration target, even
 * at a caret the combined gesture answers with Show Usages (e.g. a qualified-alias qualifier that is both
 * a reference and sits on a declaration). Decision assertions therefore use [gtduNavigationAtCaret];
 * destination assertions - "Go To Declaration lands on X" - use this.
 *
 * Shares the EP-provider front-run and internal-result unwrapping (and hence the reflection rationale) of
 * [gtduNavigationAtCaret]; a `GTDActionData.result()` is the [NavigationActionResult] directly (no GTD/SU
 * branch wrapper).
 */
fun CodeInsightTestFixture.gotoDeclarationTargetsAtCaret(): List<GtduTarget>? {
    val targetFile = file
    val offset = caretOffset
    val project = project
    val editor = editor
    return ApplicationManager.getApplication().executeOnPooledThread(Callable {
        ReadAction.nonBlocking(Callable { resolveGotoDeclarationTargets(project, editor, targetFile, offset) }).executeSynchronously()
    }).get()
}

private fun resolveGotoDeclarationTargets(project: Project, editor: Editor, targetFile: PsiFile, offset: Int): List<GtduTarget>? {
    // Faithful to GotoDeclarationOnlyHandler2: fromGTDProviders(...) ?: gotoDeclaration(file, offset), then result().
    val actionData = fromGtdProviders(project, editor, offset) ?: invokeGtd(targetFile, offset) ?: return null
    val navigationActionResult = actionData.call("result") ?: return null
    return navigationActionResult.toGtduTargets(project)
}

/**
 * Unwraps a `NavigationActionResult` (Single- or MultipleTargets) into [GtduTarget]s. Both a `SingleTarget`
 * and each `MultipleTargets`' `LazyTargetWithPresentation` expose `getRequestor()`, whose
 * `navigationRequest()` yields the destination.
 */
private fun Any.toGtduTargets(project: Project): List<GtduTarget> =
    when (javaClass.simpleName) {
        "SingleTarget" -> listOf(toGtduTarget(project))
        "MultipleTargets" -> (call("getTargets") as List<*>).mapNotNull { it?.toGtduTarget(project) }
        else -> emptyList()
    }

/** Builds a [GtduTarget] from a `SingleTarget` / `LazyTargetWithPresentation` (both share `getRequestor()`). */
private fun Any.toGtduTarget(project: Project): GtduTarget =
    GtduTarget(destination = resolveDestination(project, call("getRequestor")))

/**
 * Resolves the leaf a navigation target lands on, from its `NavigationRequestor.navigationRequest()`:
 * a [com.intellij.platform.backend.navigation.impl.SourceNavigationRequest] (or a subclass such as
 * `SharedSourceNavigationRequest`) gives a file + offset (we use the caret offset marker, so we land on
 * the same leaf the caret would move to); a
 * [com.intellij.platform.backend.navigation.impl.RawNavigationRequest] gives a [com.intellij.pom.Navigatable]
 * that is itself the target `PsiElement`. Other request kinds (e.g. directory) are not navigation-to-leaf
 * and resolve to `null`.
 */
private fun resolveDestination(project: Project, requestor: Any?): PsiElement? {
    val request = requestor?.call("navigationRequest") ?: return null
    val navImpl = "com.intellij.platform.backend.navigation.impl."
    return when {
        // SourceNavigationRequest and its subclasses (e.g. SharedSourceNavigationRequest) - file + markers.
        Class.forName("${navImpl}SourceNavigationRequest").isInstance(request) -> {
            val virtualFile = request.call("getFile") as? VirtualFile ?: return null
            val marker = request.call("getOffsetMarker") ?: request.call("getElementRangeMarker") ?: return null
            val startOffset = marker.call("getStartOffset") as Int
            PsiManager.getInstance(project).findFile(virtualFile)?.findElementAt(startOffset)
        }
        // RawNavigationRequest - wraps a Navigatable that is itself the target PsiElement.
        Class.forName("${navImpl}RawNavigationRequest").isInstance(request) ->
            request.call("getNavigatable") as? PsiElement
        else -> null
    }
}

private fun Any.call(methodName: String): Any? =
    javaClass.getMethod(methodName).apply { isAccessible = true }.invoke(this)

/** Asserts the Ctrl+Click / Ctrl+B gesture at the caret resolves to "go to declaration" (a real target). */
fun CodeInsightTestFixture.assertGotoDeclarationChosenAtCaret(message: String? = null) {
    val navigation = gtduNavigationAtCaret()
    assertTrue(
        message ?: "Ctrl+Click should choose go-to-declaration, but resolved to $navigation",
        navigation is GtduNavigation.GotoDeclaration
    )
}

/** Asserts the gesture at the caret resolves to "show usages". */
fun CodeInsightTestFixture.assertShowUsagesChosenAtCaret(message: String? = null) {
    val navigation = gtduNavigationAtCaret()
    assertTrue(
        message ?: "Ctrl+Click should choose show-usages, but resolved to $navigation",
        navigation is GtduNavigation.ShowUsages
    )
}

/** Asserts the gesture at the caret does nothing (no declaration and no usages). */
fun CodeInsightTestFixture.assertNoNavigationAtCaret(message: String? = null) {
    val navigation = gtduNavigationAtCaret()
    assertTrue(
        message ?: "Ctrl+Click should not navigate, but resolved to $navigation",
        navigation === GtduNavigation.None
    )
}

/**
 * The PSI leaf the Go To Declaration gesture actually navigates to at the caret, for the common
 * single-target case - the faithful replacement for the old caret-inspecting `gotoDeclarationDestination` /
 * `gotoDeclarationTargetElement`. Resolves the destination from the target's real `NavigationRequest`
 * (so it follows navigation into other source files or a decompiled `.beam`), rather than reading the
 * caret after `performEditorAction` (which, in a decompiled `.beam`, cannot tell "went nowhere" from
 * "landed on the definition" - see [gtduNavigationAtCaret]). Fails if the gesture did not resolve to
 * exactly one target.
 */
fun CodeInsightTestFixture.gotoDeclarationDestinationAtCaret(): PsiElement? = gotoDeclarationSingleTargetAtCaret().destination

private fun CodeInsightTestFixture.gotoDeclarationSingleTargetAtCaret(): GtduTarget {
    val targets = gotoDeclarationTargetsAtCaret()
        ?: throw AssertionError("Expected Go To Declaration to resolve a target, but it navigated nowhere")
    return targets.singleOrNull()
        ?: throw AssertionError("Expected a single Go To Declaration target, got ${targets.size}")
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
 * Drives completion at the caret for the **single-candidate** case: with a non-empty prefix a lone
 * match auto-inserts instead of opening the popup. Asserts no multi-candidate popup was shown (i.e.
 * [complete] returned `null`) and returns the resulting document text, so callers can assert what the
 * sole completion inserted.
 *
 * Complements [completionStringsAtCaret], which covers the multi-candidate popup case (it also returns
 * `null` on auto-insert, but discards the inserted text this helper exposes).
 */
fun CodeInsightTestFixture.completeSoleCandidateAtCaret(): String {
    assertNull(
        "Expected a single auto-inserted completion, but a multi-candidate popup was shown",
        complete(CompletionType.BASIC)
    )
    return file.text
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

/**
 * Renames the symbol at the caret the way the user-facing Rename refactoring (Shift+F6) does.
 *
 * The rename target is resolved from the caret exactly as the platform's Symbol rename handler
 * resolves it: [targetSymbols] is the same resolution that feeds the `SYMBOLS` data key the
 * handler reads, and every renameable Elixir symbol implements
 * [com.intellij.refactoring.rename.api.RenameTarget] directly. The target is then handed to
 * [CodeInsightTestFixture.renameTarget] - the platform's test entry point for the
 * `RenameTarget`-based rename pipeline (the same drive JetBrains' own new-rename-API tests use),
 * which runs the production search (the registered `RenameUsageSearcher` → queries → file updates
 * in a write command) headlessly and unmocked. Callers assert on the resulting document text.
 */
@Suppress("UnstableApiUsage")
fun CodeInsightTestFixture.renameTargetAtCaret(newName: String) {
    val targets = targetSymbols(file, caretOffset).filterIsInstance<RenameTarget>().distinct()
    val target = targets.singleOrNull()
        ?: throw AssertionError("Expected exactly one rename target at the caret, got ${targets.size}: $targets")
    renameTarget(target, newName)
}
