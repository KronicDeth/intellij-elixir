package org.elixir_lang.model.psi.module_attribute

import com.intellij.model.Symbol
import com.intellij.model.psi.PsiExternalReferenceHost
import com.intellij.model.psi.PsiSymbolReference
import com.intellij.model.psi.PsiSymbolReferenceHints
import com.intellij.model.psi.PsiSymbolReferenceProvider
import com.intellij.model.search.SearchRequest
import com.intellij.openapi.project.Project
import com.intellij.util.concurrency.annotations.RequiresReadLock
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.isModuleAttributeNameElement

@Suppress("UnstableApiUsage")
internal class ModuleAttributeReferenceProvider : PsiSymbolReferenceProvider {
    @RequiresReadLock
    override fun getReferences(
        element: PsiExternalReferenceHost,
        hints: PsiSymbolReferenceHints
    ): Collection<PsiSymbolReference> {
        if (element !is Call) return emptyList()
        if (!element.isModuleAttributeNameElement()) return emptyList()
        if (element.containingFile.virtualFile?.fileType == org.elixir_lang.leex.file.Type.INSTANCE) return emptyList()

        if (ModuleAttributeReference.resolveSymbols(element).isEmpty()) return emptyList()
        val nameElement = ModuleAttributeSymbol.nameIdentifierElement(element) ?: return emptyList()
        val rangeInElement = nameElement.textRange.shiftLeft(element.textRange.startOffset)

        return listOf(ModuleAttributeReference(element, rangeInElement))
    }

    override fun getSearchRequests(project: Project, target: Symbol): Collection<SearchRequest> = emptyList()
}
