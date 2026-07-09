package org.elixir_lang.model.psi.function

import com.intellij.model.Symbol
import com.intellij.model.psi.PsiExternalReferenceHost
import com.intellij.model.psi.PsiSymbolReference
import com.intellij.model.psi.PsiSymbolReferenceHints
import com.intellij.model.psi.PsiSymbolReferenceProvider
import com.intellij.model.search.SearchRequest
import com.intellij.openapi.project.Project
import com.intellij.util.concurrency.annotations.RequiresReadLock
import org.elixir_lang.model.psi.FunctionArityKeywordPair
import org.elixir_lang.psi.call.Call

/**
 * Attaches a [FunctionArityKeywordPairReference] to the **key** of every `name: arity` keyword pair
 * governed by a recognized directive host (`import :only`/`:except`, `@compile :inline`, `@dialyzer`,
 * `defoverridable`). See [FunctionArityKeywordPair].
 *
 * The reference is anchored on the *host* [Call] (the reference-host element the platform hands us)
 * with a `rangeInElement` covering just the keyword key, so Ctrl+Click / Go To Declaration on the key
 * navigates to the function (or `@callback` for `defoverridable`), and in-place rename started at the
 * key finds a rename target.
 *
 * [getSearchRequests] returns nothing on purpose: reverse Find Usages (function/callback -> keyword
 * key) is served by [org.elixir_lang.model.psi.ElixirSymbolUsageSearcher]'s direct word search, so
 * these references are not text-searched during Find Usages.
 */
@Suppress("UnstableApiUsage")
internal class FunctionArityKeywordPairReferenceProvider : PsiSymbolReferenceProvider {
    @RequiresReadLock
    override fun getReferences(
        element: PsiExternalReferenceHost,
        hints: PsiSymbolReferenceHints
    ): Collection<PsiSymbolReference> {
        if (element !is Call) return emptyList()
        return FunctionArityKeywordPair.occurrencesIn(element).map { occurrence ->
            val rangeInElement = occurrence.keyRange.shiftLeft(element.textRange.startOffset)
            FunctionArityKeywordPairReference(element, rangeInElement, occurrence.pair)
        }
    }

    override fun getSearchRequests(project: Project, target: Symbol): Collection<SearchRequest> = emptyList()
}
