package org.elixir_lang.semantic.modular

import com.intellij.psi.PsiElement
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import org.elixir_lang.CanonicallyNamed
import org.elixir_lang.CanonicallyNamed.Companion.CANONICAL_NAME_SET

interface CanonicallyNamedMaybeEnclosed : CanonicallyNamed, MaybeEnclosed {
    val name: String?

    companion object {
        fun getCanonicalNameSet(
            canonicallyNamedMaybeEnclosed: CanonicallyNamedMaybeEnclosed,
            psiElement: PsiElement
        ): Set<String> =
            CachedValuesManager.getCachedValue(psiElement, CANONICAL_NAME_SET) {
                CachedValueProvider.Result.create(computeCanonicalNameSet(canonicallyNamedMaybeEnclosed), psiElement)
            }

        private fun computeCanonicalNameSet(canonicallyNamedMaybeEnclosed: CanonicallyNamedMaybeEnclosed): Set<String> =
            canonicallyNamedMaybeEnclosed
                .name?.let { name ->
                    canonicallyNamedMaybeEnclosed
                        .enclosingModular
                        ?.canonicalNameSet?.map { enclosingCanonicalName ->
                            "${enclosingCanonicalName}.${name}"
                        }
                        ?.toSet()
                        ?: setOf(name)
                }
                ?: emptySet()
    }
}
