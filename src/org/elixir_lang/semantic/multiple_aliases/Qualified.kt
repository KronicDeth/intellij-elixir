package org.elixir_lang.semantic.multiple_aliases

import com.intellij.openapi.util.Key
import com.intellij.psi.ElementDescriptionLocation
import com.intellij.psi.PsiElement
import com.intellij.psi.util.CachedValue
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import org.elixir_lang.psi.QualifiedMultipleAliases
import org.elixir_lang.psi.impl.stripAccessExpression
import org.elixir_lang.semantic.Alias
import org.elixir_lang.semantic.MultipleAliases
import org.elixir_lang.semantic.semantic

class Qualified(val qualifiedMultipleAliases: QualifiedMultipleAliases) : MultipleAliases {
    override val psiElement: PsiElement
        get() = qualifiedMultipleAliases
    override val aliases: List<Alias>
        get() = CachedValuesManager.getCachedValue(qualifiedMultipleAliases, ALIASES) {
            CachedValueProvider.Result.create(computeAliases(qualifiedMultipleAliases), qualifiedMultipleAliases)
        }
    override val suffixes: List<Alias>
        get() = CachedValuesManager.getCachedValue(qualifiedMultipleAliases, SUFFIXES) {
            CachedValueProvider.Result.create(computeSuffixes(this), qualifiedMultipleAliases)
        }

    override fun elementDescription(location: ElementDescriptionLocation): String? {
        TODO("Not yet implemented")
    }

    companion object {
        private val ALIASES: Key<CachedValue<List<Alias>>> = Key("elixir.multiple_aliases.aliases")

        private fun computeAliases(qualifiedMultipleAliases: QualifiedMultipleAliases): List<Alias> =
            qualifiedMultipleAliases.multipleAliases.children.mapNotNull {
                @Suppress("UNCHECKED_CAST")
                it.stripAccessExpression().semantic as? Alias
            }

        private val SUFFIXES: Key<CachedValue<List<Alias>>> = Key("elixir.multiple_aliases.suffixes")

        private fun computeSuffixes(qualified: Qualified): List<Alias> =
            qualified.aliases.mapNotNull(Alias::suffix)
    }
}
