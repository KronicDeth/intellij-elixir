package org.elixir_lang

import com.intellij.openapi.util.Key
import com.intellij.psi.PsiElement
import com.intellij.psi.util.CachedValue
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager

interface CanonicallyNamed {
    /**
     * @return {@code null} if it does not have a canonical name OR if it has more than one canonical name
     */
    val canonicalName: String?

    /**
     * @return empty set if no canonical names
     */
    val canonicalNameSet: Set<String>

    companion object {
        fun getCanonicalName(canonicallyNamed: CanonicallyNamed, psiElement: PsiElement) =
            CachedValuesManager.getCachedValue(psiElement, CANONICAL_NAME) {
                CachedValueProvider.Result.create(computeCanonicalName(canonicallyNamed), psiElement)
            }

        private val CANONICAL_NAME: Key<CachedValue<String?>> = Key("elixir.canonically_named.canonical_name")

        private fun computeCanonicalName(canonicallyNamed: CanonicallyNamed): String? =
            canonicallyNamed.canonicalNameSet.singleOrNull()

        val CANONICAL_NAME_SET: Key<CachedValue<Set<String>>> =
            Key("elixir.canonically_named.canonical_name_set")
    }
}
