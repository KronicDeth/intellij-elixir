package org.elixir_lang.model.psi.generic_server

import com.intellij.model.Symbol
import com.intellij.model.psi.PsiSymbolReference
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.util.concurrency.annotations.RequiresReadLock
import org.elixir_lang.psi.ElixirAtom

/**
 * A forward-navigation reference from a GenServer / `Process` message atom (the send site) to the
 * matching handler clause(s). See [GenServerDispatch] for the supported dispatch trios.
 */
@Suppress("UnstableApiUsage")
class GenServerRequestReference(
    private val atom: ElixirAtom,
    private val rangeInElement: TextRange
) : PsiSymbolReference {
    override fun getElement(): PsiElement = atom

    override fun getRangeInElement(): TextRange = rangeInElement

    @RequiresReadLock
    override fun resolveReference(): Collection<Symbol> =
        GenServerDispatch.handlerTargetsForRequestAtom(atom)
}
