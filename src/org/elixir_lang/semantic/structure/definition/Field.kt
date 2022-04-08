package org.elixir_lang.semantic.structure.definition

import com.intellij.openapi.util.Key
import com.intellij.psi.ElementDescriptionLocation
import com.intellij.psi.PsiElement
import com.intellij.psi.util.CachedValue
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import org.elixir_lang.Presentable
import org.elixir_lang.psi.ElixirAtom
import org.elixir_lang.psi.QuotableKeywordPair
import org.elixir_lang.semantic.Semantic
import org.elixir_lang.semantic.structure.Definition

class Field(val structure: Definition, override val psiElement: PsiElement) : Semantic, Presentable {
    override val presentation: org.elixir_lang.navigation.item_presentation.structure.Field
        get() = CachedValuesManager.getCachedValue(psiElement, PRESENTATION) {
            CachedValueProvider.Result.create(computePresentation(this), psiElement)
        }

    override fun elementDescription(location: ElementDescriptionLocation): String? {
        TODO("Not yet implemented")
    }

    companion object {
        private val PRESENTATION: Key<CachedValue<org.elixir_lang.navigation.item_presentation.structure.Field>> =
            Key("elixir.semantic.structure.definition.presentation")

        private fun computePresentation(field: Field):
                org.elixir_lang.navigation.item_presentation.structure.Field {
            val location = field.structure.presentation.locatedPresentableText

            val (name, defaultValue) = when (val psiElement = field.psiElement) {
                is ElixirAtom -> Pair(psiElement.text.substring(1), "nil")
                is QuotableKeywordPair -> Pair(psiElement.keywordKey.text, psiElement.keywordValue.text)
                else -> TODO()
            }

            return org.elixir_lang.navigation.item_presentation.structure.Field(location, name, defaultValue)
        }
    }
}
