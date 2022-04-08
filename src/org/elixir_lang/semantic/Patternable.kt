package org.elixir_lang.semantic

import com.intellij.openapi.util.Key
import com.intellij.psi.PsiElement
import com.intellij.psi.util.CachedValue
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager

/**
 * Can occur in a pattern as a pattern match literal
 */
interface Patternable : Semantic {
    val outerMostPattern: Pattern?
        get() = CachedValuesManager.getCachedValue(psiElement, OUTER_MOST_PATTERN) {
            CachedValueProvider.Result.create(computeOuterMostPattern(psiElement), psiElement)
        }

    companion object {
        private val OUTER_MOST_PATTERN: Key<CachedValue<Pattern?>> =
            Key("elixir.semantic.patternable.outer_most_pattern")

        private fun computeOuterMostPattern(psiElement: PsiElement): Pattern? =
            psiElement.semanticParent?.let { semanticParent ->
                when (semanticParent) {
                    is Patternable -> semanticParent.outerMostPattern
                    else -> TODO()
                }
            }
    }
}
