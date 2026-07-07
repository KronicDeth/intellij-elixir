package org.elixir_lang.model.psi.module

import com.intellij.model.Symbol
import com.intellij.model.psi.PsiSymbolDeclaration
import com.intellij.model.psi.PsiSymbolDeclarationProvider
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.concurrency.annotations.RequiresReadLock
import org.elixir_lang.psi.Module
import org.elixir_lang.psi.call.Call

@Suppress("UnstableApiUsage")
internal class ModuleSymbolDeclarationProvider : PsiSymbolDeclarationProvider {
    @RequiresReadLock
    override fun getDeclarations(element: PsiElement, offsetInElement: Int): Collection<PsiSymbolDeclaration> {
        val moduleCall = generateSequence(element) { it.parent }
            .filterIsInstance<Call>()
            .firstOrNull { Module.`is`(it) }
            ?: return emptyList()

        val nameElement = ModuleSymbol.moduleNameElement(moduleCall) ?: return emptyList()
        if (!PsiTreeUtil.isAncestor(nameElement, element, false)) return emptyList()

        val symbol = ModuleSymbol.fromModular(moduleCall) ?: return emptyList()
        val rangeInDeclaringElement = nameElement.textRange.shiftLeft(moduleCall.textRange.startOffset)

        return listOf(object : PsiSymbolDeclaration {
            override fun getDeclaringElement(): PsiElement = moduleCall
            override fun getRangeInDeclaringElement(): TextRange = rangeInDeclaringElement
            override fun getSymbol(): Symbol = symbol
        })
    }
}
