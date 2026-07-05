package org.elixir_lang.model.psi.callback

import com.intellij.model.Symbol
import com.intellij.model.psi.PsiExternalReferenceHost
import com.intellij.model.psi.PsiSymbolReference
import com.intellij.model.psi.PsiSymbolReferenceHints
import com.intellij.model.psi.PsiSymbolReferenceProvider
import com.intellij.model.search.SearchRequest
import com.intellij.openapi.project.Project
import com.intellij.util.concurrency.annotations.RequiresReadLock
import org.elixir_lang.psi.CallDefinitionClause
import org.elixir_lang.psi.call.Call

/**
 * Attaches a [CallbackImplReference] to an implementing `def`/`defmacro` clause's name, so
 * "Go To Declaration" navigates to the `@callback` it implements.
 *
 * Fast-exits for anything that is not a call-definition clause, leaving all other navigation
 * unchanged. Resolution is lazy: the reference is returned unconditionally for call-def clauses and
 * the platform calls [CallbackImplReference.resolveReference] only when it needs to offer navigation
 * (Ctrl+Click, Go To Declaration). An empty resolution simply produces no navigation target - there
 * are no phantom unresolved references.
 *
 * [getSearchRequests] returns nothing on purpose: callback → implementations (reverse Find Usages) is
 * served by [org.elixir_lang.model.psi.ElixirSymbolUsageSearcher]'s direct query, so these references
 * are not text-searched during Find Usages - avoiding double-listing each implementation.
 */
@Suppress("UnstableApiUsage")
internal class CallbackImplReferenceProvider : PsiSymbolReferenceProvider {
    @RequiresReadLock
    override fun getReferences(element: PsiExternalReferenceHost, hints: PsiSymbolReferenceHints): Collection<PsiSymbolReference> {
        if (element !is Call || !CallDefinitionClause.`is`(element)) return emptyList()
        val nameIdentifier = CallDefinitionClause.nameIdentifier(element) ?: return emptyList()
        val rangeInElement = nameIdentifier.textRange.shiftLeft(element.textRange.startOffset)
        return listOf(CallbackImplReference(element, rangeInElement))
    }

    override fun getSearchRequests(project: Project, target: Symbol): Collection<SearchRequest> = emptyList()
}
