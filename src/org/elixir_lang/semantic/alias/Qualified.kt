package org.elixir_lang.semantic.alias

import com.intellij.openapi.util.Key
import com.intellij.psi.ElementDescriptionLocation
import com.intellij.psi.PsiElement
import com.intellij.psi.util.CachedValue
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import org.elixir_lang.psi.QualifiedAlias
import org.elixir_lang.semantic.Alias
import org.elixir_lang.semantic.semantic

class Qualified(val qualifiedAlias: QualifiedAlias) : Alias {
    override val psiElement: PsiElement
        get() = qualifiedAlias

    override fun elementDescription(location: ElementDescriptionLocation): String? {
        TODO("Not yet implemented")
    }

    override val name: String
        get() = TODO("Not yet implemented")
    override val suffix: Alias?
        get() = CachedValuesManager.getCachedValue(qualifiedAlias, SUFFIX) {
            CachedValueProvider.Result.create(computeSuffix(qualifiedAlias), qualifiedAlias)
        }
    override val nested: Set<Alias>
        get() = TODO("Not yet implemented")

    companion object {
        private val SUFFIX: Key<CachedValue<Alias>> = Key("elixir.alias.suffix")

        private fun computeSuffix(qualifiedAlias: QualifiedAlias): Alias? =
            qualifiedAlias.getAlias().semantic as? Alias
    }
}
