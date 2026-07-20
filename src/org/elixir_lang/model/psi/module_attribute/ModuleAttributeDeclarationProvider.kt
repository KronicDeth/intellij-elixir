package org.elixir_lang.model.psi.module_attribute

import com.intellij.model.Symbol
import com.intellij.model.psi.PsiSymbolDeclaration
import com.intellij.model.psi.PsiSymbolDeclarationProvider
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.concurrency.annotations.RequiresReadLock
import org.elixir_lang.psi.AtUnqualifiedNoParenthesesCall
import org.elixir_lang.psi.impl.identifierTextRange

@Suppress("UnstableApiUsage")
internal class ModuleAttributeDeclarationProvider : PsiSymbolDeclarationProvider {
    @RequiresReadLock
    override fun getDeclarations(element: PsiElement, offsetInElement: Int): Collection<PsiSymbolDeclaration> {
        val declaration = generateSequence(element) { it.parent }
            .filterIsInstance<AtUnqualifiedNoParenthesesCall<*>>()
            .firstOrNull()
            ?: return emptyList()
        val symbol = ModuleAttributeSymbol.fromDeclaration(declaration) ?: return emptyList()
        val nameIdentifier = declaration.atIdentifier
        val inName = when {
            PsiTreeUtil.isAncestor(nameIdentifier, element, false) -> true
            element == declaration -> {
                val offsetInFile = element.textRange.startOffset + offsetInElement
                nameIdentifier.textRange.containsOffset(offsetInFile)
            }
            else -> false
        }
        if (!inName) return emptyList()

        val rangeInDeclaringElement = nameIdentifier.identifierTextRange().shiftLeft(declaration.textRange.startOffset)
        return listOf(object : PsiSymbolDeclaration {
            override fun getDeclaringElement(): PsiElement = declaration
            override fun getRangeInDeclaringElement(): TextRange = rangeInDeclaringElement
            override fun getSymbol(): Symbol = symbol
        })
    }
}
