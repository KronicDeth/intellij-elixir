package org.elixir_lang.psi.scope

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.psi.PsiElement

/**
 * Keeps track of the best `PsiElement` to use for a given `LookupElement` `lookupName`.
 */
class LookupElementByLookupName {
    fun contains(lookupName: String): Boolean = lookupElementByLookupName.contains(lookupName)

    fun put(lookupName: String, element: PsiElement) {
        lookupElementByLookupName.computeIfAbsent(lookupName) {
            LookupElementBuilder.createWithSmartPointer(it, element)
        }
    }

    fun lookupElements(): Collection<LookupElement> = lookupElementByLookupName.values

    private val lookupElementByLookupName = mutableMapOf<String, LookupElement>()
}
