package org.elixir_lang.model.psi.variable

import com.intellij.model.Symbol
import com.intellij.model.psi.PsiExternalReferenceHost
import com.intellij.model.psi.PsiSymbolReference
import com.intellij.model.psi.PsiSymbolReferenceHints
import com.intellij.model.psi.PsiSymbolReferenceProvider
import com.intellij.model.search.SearchRequest
import com.intellij.openapi.project.Project
import com.intellij.util.concurrency.annotations.RequiresReadLock
import org.elixir_lang.psi.ElixirVariable
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.operation.Match

@Suppress("UnstableApiUsage")
internal class VariableReferenceProvider : PsiSymbolReferenceProvider {
    @RequiresReadLock
    override fun getReferences(
        element: PsiExternalReferenceHost,
        hints: PsiSymbolReferenceHints
    ): Collection<PsiSymbolReference> {
        if (element !is Call && element !is ElixirVariable) return emptyList()
        val host = when (element) {
            is ElixirVariable -> element
            is Call -> element
            else -> return emptyList()
        }
        if (host is Match) return emptyList()
        if (VariableSymbol.isDeclaration(host)) return emptyList()
        if (VariableReference.resolveSymbols(host).isEmpty()) return emptyList()
        val nameElement = VariableSymbol.nameIdentifierElement(host) ?: return emptyList()
        val rangeInElement = nameElement.textRange.shiftLeft(host.textRange.startOffset)

        return listOf(VariableReference(host, rangeInElement))
    }

    override fun getSearchRequests(project: Project, target: Symbol): Collection<SearchRequest> = emptyList()
}
