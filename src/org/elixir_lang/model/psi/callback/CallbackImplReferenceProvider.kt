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
import org.elixir_lang.psi.Protocol
import org.elixir_lang.psi.call.Call

/**
 * Attaches a [CallbackImplReference] to an implementing `def`/`defmacro` clause's name, so
 * "Go To Declaration" navigates to the `@callback` it implements.
 *
 * Only creates a reference when the enclosing module is known to implement at least one behaviour
 * (`@behaviour Foo` or `use Foo`). A non-null reference that resolves to nothing would shadow the
 * `FunctionSymbol` declaration via `referencedData ?: declaredData`, breaking Ctrl+Click / Find
 * Usages on ordinary functions.
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
        // A `def`/`defmacro` directly inside a `defprotocol` is a protocol function *declaration*,
        // not a callback implementation. Attaching a reference here (which resolves to nothing) would
        // create a non-null-but-empty `referencedData` that shadows the `ProtocolFunction` declaration
        // in the platform's `targetSymbols` (`referencedData ?: declaredData`), breaking Find Usages.
        val enclosingModular = CallDefinitionClause.enclosingModularMacroCall(element)
        if (enclosingModular == null || Protocol.`is`(enclosingModular)) return emptyList()
        // A non-null reference that resolves to nothing would shadow the `FunctionSymbol` declaration.
        // Only create the reference for defs in modules that actually implement a behaviour.
        if (BehaviourMembership.namesImplementedBy(enclosingModular).isEmpty()) return emptyList()
        val nameIdentifier = CallDefinitionClause.nameIdentifier(element) ?: return emptyList()
        val rangeInElement = nameIdentifier.textRange.shiftLeft(element.textRange.startOffset)
        return listOf(CallbackImplReference(element, rangeInElement))
    }

    override fun getSearchRequests(project: Project, target: Symbol): Collection<SearchRequest> = emptyList()
}
