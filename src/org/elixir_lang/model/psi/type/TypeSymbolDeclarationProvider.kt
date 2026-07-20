package org.elixir_lang.model.psi.type

import com.intellij.model.Symbol
import com.intellij.model.psi.PsiSymbolDeclaration
import com.intellij.model.psi.PsiSymbolDeclarationProvider
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.concurrency.annotations.RequiresReadLock
import org.elixir_lang.psi.AtUnqualifiedNoParenthesesCall
import org.elixir_lang.structure_view.element.Type as TypeElement

@Suppress("UnstableApiUsage")
internal class TypeSymbolDeclarationProvider : PsiSymbolDeclarationProvider {
    @RequiresReadLock
    override fun getDeclarations(element: PsiElement, offsetInElement: Int): Collection<PsiSymbolDeclaration> {
        val typeAttribute = generateSequence(element) { it.parent }
            .filterIsInstance<AtUnqualifiedNoParenthesesCall<*>>()
            .firstOrNull { TypeElement.`is`(it) }
            ?: return emptyList()
        val nameIdentifier = TypeElement.nameIdentifier(typeAttribute) ?: return emptyList()
        val inName = when {
            PsiTreeUtil.isAncestor(nameIdentifier, element, false) -> true
            element == typeAttribute -> {
                val offsetInFile = element.textRange.startOffset + offsetInElement
                nameIdentifier.textRange.containsOffset(offsetInFile)
            }
            else -> false
        }
        if (!inName) return emptyList()

        val symbols = TypeSymbol.fromTypeAttribute(typeAttribute)
        if (symbols.isEmpty()) return emptyList()
        val rangeInDeclaringElement = nameIdentifier.textRange.shiftLeft(typeAttribute.textRange.startOffset)

        return symbols.map { symbol ->
            object : PsiSymbolDeclaration {
                override fun getDeclaringElement(): PsiElement = typeAttribute
                override fun getRangeInDeclaringElement(): TextRange = rangeInDeclaringElement
                override fun getSymbol(): Symbol = symbol
            }
        }
    }
}
