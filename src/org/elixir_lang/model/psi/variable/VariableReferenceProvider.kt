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
import org.elixir_lang.psi.UnqualifiedBracketOperation
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.operation.Match

@Suppress("UnstableApiUsage")
internal class VariableReferenceProvider : PsiSymbolReferenceProvider {
    @RequiresReadLock
    override fun getReferences(
        element: PsiExternalReferenceHost,
        hints: PsiSymbolReferenceHints
    ): Collection<PsiSymbolReference> {
        val host = when (element) {
            is ElixirVariable, is Call, is UnqualifiedBracketOperation -> element
            else -> return emptyList()
        }
        if (host is Match) return emptyList()
        val nameElement = VariableSymbol.nameIdentifierElement(host) ?: return emptyList()
        val rangeInElement = nameElement.textRange.shiftLeft(host.textRange.startOffset)
        // The service drops references that do not contain the hinted offset, so a caret outside the name (every
        // enclosing call of a declaration, walked on the way up to the file) is answered without resolving.
        val offsetInElement = hints.offsetInElement
        if (offsetInElement >= 0 && !rangeInElement.containsOffset(offsetInElement)) return emptyList()
        if (VariableSymbol.isDeclaration(host)) return emptyList()
        if (VariableReference.resolveSymbols(host).isEmpty()) return emptyList()

        return listOf(VariableReference(host, rangeInElement))
    }

    override fun getSearchRequests(project: Project, target: Symbol): Collection<SearchRequest> = emptyList()
}
