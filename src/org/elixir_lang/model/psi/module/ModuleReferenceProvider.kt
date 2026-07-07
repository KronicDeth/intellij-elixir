package org.elixir_lang.model.psi.module

import com.intellij.model.Symbol
import com.intellij.model.psi.PsiExternalReferenceHost
import com.intellij.model.psi.PsiSymbolReference
import com.intellij.model.psi.PsiSymbolReferenceHints
import com.intellij.model.psi.PsiSymbolReferenceProvider
import com.intellij.model.search.SearchRequest
import com.intellij.openapi.project.Project
import com.intellij.util.concurrency.annotations.RequiresReadLock
import org.elixir_lang.psi.QualifiableAlias
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.name.Function
import org.elixir_lang.psi.call.name.Module.KERNEL
import org.elixir_lang.psi.impl.call.finalArguments
import org.elixir_lang.psi.impl.stripAccessExpression

@Suppress("UnstableApiUsage")
internal class ModuleReferenceProvider : PsiSymbolReferenceProvider {
    @RequiresReadLock
    override fun getReferences(
        element: PsiExternalReferenceHost,
        hints: PsiSymbolReferenceHints
    ): Collection<PsiSymbolReference> {
        if (element !is Call) return emptyList()
        val alias = moduleReferenceAlias(element) ?: return emptyList()

        val nameIdentifier = alias.nameIdentifier ?: return emptyList()
        val rangeInElement = nameIdentifier.textRange.shiftLeft(element.textRange.startOffset)

        return listOf(ModuleReference(element, alias, rangeInElement))
    }

    override fun getSearchRequests(project: Project, target: Symbol): Collection<SearchRequest> = emptyList()

    @RequiresReadLock
    private fun moduleReferenceAlias(call: Call): QualifiableAlias? =
        if (call.isCallingMacro(KERNEL, Function.ALIAS) ||
            call.isCallingMacro(KERNEL, Function.USE) ||
            call.isCallingMacro(KERNEL, Function.IMPORT)
        ) {
            call.finalArguments()
                ?.firstOrNull()
                ?.stripAccessExpression() as? QualifiableAlias
        } else {
            null
        }
}
