package org.elixir_lang.heex.xml

import com.intellij.model.Symbol
import com.intellij.model.psi.PsiExternalReferenceHost
import com.intellij.model.psi.PsiSymbolReference
import com.intellij.model.psi.PsiSymbolReferenceHints
import com.intellij.model.psi.PsiSymbolReferenceProvider
import com.intellij.model.search.SearchRequest
import com.intellij.openapi.project.Project
import com.intellij.psi.xml.XmlTag
import com.intellij.psi.xml.XmlToken
import com.intellij.psi.xml.XmlTokenType
import com.intellij.util.concurrency.annotations.RequiresReadLock
import org.elixir_lang.heex.isInHeex
import org.elixir_lang.model.psi.function.FunctionSymbol

/**
 * References a HEEx component tag's name tokens to the [FunctionSymbol] of its resolved
 * `def`/`defp`, so Rename works with the caret on the tag. [HeexComponentDescriptor]'s legacy
 * `TagNameReference` only gives navigation: `def`/`defp` clauses belong to the Symbol API
 * ([org.elixir_lang.TargetElementEvaluator] excludes them from legacy rename), so the tag needs a
 * Symbol reference for the rename pipeline to find a target at its position.
 */
@Suppress("UnstableApiUsage")
internal class HeexComponentSymbolReferenceProvider : PsiSymbolReferenceProvider {
    @RequiresReadLock
    override fun getReferences(
        element: PsiExternalReferenceHost,
        hints: PsiSymbolReferenceHints
    ): Collection<PsiSymbolReference> {
        val tag = element as? XmlTag ?: return emptyList()
        if (!tag.isInHeex()) return emptyList()
        // The clause's first symbol is the one FunctionSymbolDeclarationProvider exposes at the
        // declaration; arities of one clause rename together.
        val symbol = HeexComponentResolver.resolveFunctionSymbols(tag).firstOrNull() ?: return emptyList()

        return tag.node.getChildren(null)
            .asSequence()
            .filter { it.elementType == XmlTokenType.XML_NAME }
            .mapNotNull { it.psi as? XmlToken }
            .map { nameToken ->
                HeexComponentSymbolReference(tag, nameToken.textRange.shiftLeft(tag.textRange.startOffset), symbol)
            }
            .toList()
    }

    // Symbol -> tag usages is served by ElixirSymbolUsageSearcher's word search; a request here
    // would double-count them.
    override fun getSearchRequests(project: Project, target: Symbol): Collection<SearchRequest> = emptyList()
}
