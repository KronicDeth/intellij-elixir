package org.elixir_lang.model.psi.variable

import com.intellij.model.Symbol
import com.intellij.model.psi.PsiSymbolDeclaration
import com.intellij.model.psi.PsiSymbolDeclarationProvider
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.concurrency.annotations.RequiresReadLock

@Suppress("UnstableApiUsage")
internal class VariableSymbolDeclarationProvider : PsiSymbolDeclarationProvider {
    @RequiresReadLock
    override fun getDeclarations(element: PsiElement, offsetInElement: Int): Collection<PsiSymbolDeclaration> {
        val declarationElement = sequenceOf(element, element.parent)
            .filterNotNull()
            .firstOrNull { candidate -> VariableSymbol.fromDeclaration(candidate) != null }
            ?: return emptyList()
        val symbol = VariableSymbol.fromDeclaration(declarationElement) ?: return emptyList()
        val nameIdentifier = VariableSymbol.nameIdentifierElement(declarationElement) ?: return emptyList()
        val inName = when {
            PsiTreeUtil.isAncestor(nameIdentifier, element, false) -> true
            element == declarationElement -> {
                val offsetInFile = declarationElement.textRange.startOffset + offsetInElement
                nameIdentifier.textRange.containsOffset(offsetInFile)
            }
            else -> false
        }
        if (!inName) return emptyList()
        val rangeInDeclaringElement = nameIdentifier.textRange.shiftLeft(declarationElement.textRange.startOffset)

        return listOf(
            object : PsiSymbolDeclaration {
                override fun getDeclaringElement(): PsiElement = declarationElement
                override fun getRangeInDeclaringElement(): TextRange = rangeInDeclaringElement
                override fun getSymbol(): Symbol = symbol
            }
        )
    }
}
