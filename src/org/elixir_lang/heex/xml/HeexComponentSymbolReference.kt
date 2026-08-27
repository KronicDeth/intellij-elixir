package org.elixir_lang.heex.xml

import com.intellij.model.Symbol
import com.intellij.model.psi.PsiSymbolReference
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.xml.XmlTag
import com.intellij.util.concurrency.annotations.RequiresReadLock
import org.elixir_lang.model.psi.function.FunctionSymbol

/** One `XML_NAME` token of a component tag (`<.button>` or `</.button>`) referencing its [FunctionSymbol]. */
@Suppress("UnstableApiUsage")
class HeexComponentSymbolReference(
    private val tag: XmlTag,
    private val rangeInElement: TextRange,
    private val symbol: FunctionSymbol
) : PsiSymbolReference {
    override fun getElement(): PsiElement = tag

    override fun getRangeInElement(): TextRange = rangeInElement

    @RequiresReadLock
    override fun resolveReference(): Collection<Symbol> = listOf(symbol)
}
