package org.elixir_lang.semantic.structure

import com.intellij.openapi.util.Key
import com.intellij.psi.ElementDescriptionLocation
import com.intellij.psi.PsiElement
import com.intellij.psi.util.CachedValue
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.usageView.UsageViewTypeLocation
import org.elixir_lang.Presentable
import org.elixir_lang.navigation.item_presentation.Parent
import org.elixir_lang.navigation.item_presentation.structure.Structure
import org.elixir_lang.psi.ElixirAtom
import org.elixir_lang.psi.ElixirList
import org.elixir_lang.psi.QuotableKeywordList
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.impl.call.finalArguments
import org.elixir_lang.psi.impl.stripAccessExpression
import org.elixir_lang.semantic.Modular
import org.elixir_lang.semantic.Semantic
import org.elixir_lang.semantic.modular.Enclosed
import org.elixir_lang.semantic.semantic
import org.elixir_lang.semantic.structure.definition.Field

class Definition(override val enclosingModular: Modular, val call: Call) : Enclosed, Presentable, Semantic {
    override val psiElement: PsiElement
        get() = call
    val fields: List<Field>
        get() = CachedValuesManager.getCachedValue(psiElement, FIELDS) {
            CachedValueProvider.Result.create(computeFields(this), psiElement)
        }
    override val presentation: Structure
        get() = CachedValuesManager.getCachedValue(psiElement, PRESENTATION) {
            CachedValueProvider.Result.create(computePresentation(this), psiElement)
        }

    override fun elementDescription(location: ElementDescriptionLocation): String? =
        when (location) {
            UsageViewTypeLocation.INSTANCE -> "struct"
            else -> null
        }

    companion object {
        private val FIELDS: Key<CachedValue<List<Field>>> = Key("elixir.semantic.structure.definition.fields")

        private fun computeFields(definition: Definition): List<Field> =
            definition
                .call
                .finalArguments()
                ?.firstOrNull()
                ?.stripAccessExpression()
                ?.let { finalArgument ->
                    computeFields(finalArgument)

                }
                ?: emptyList()

        private fun computeFields(psiElement: PsiElement): List<Field> =
            when (psiElement) {
                is QuotableKeywordList -> computeFields(psiElement)
                is ElixirList -> computeFields(psiElement)
                else -> emptyList()
            }

        private fun computeFields(keywordList: QuotableKeywordList): List<Field> =
            keywordList
                .quotableKeywordPairList()
                .mapNotNull { it.semantic as? Field }

        private fun computeFields(list: ElixirList): List<Field> =
            list
                .children
                .map { it.stripAccessExpression() }
                .flatMap { child ->
                    when (child) {
                        is QuotableKeywordList -> computeFields(child)
                        is ElixirAtom -> computeFields(child)
                        else -> emptyList()
                    }
                }

        private fun computeFields(atom: ElixirAtom): List<Field> =
            atom
                .semantic.let { it as? Field }
                ?.let { listOf(it) }
                ?: emptyList()

        private val PRESENTATION: Key<CachedValue<Structure>> =
            Key("elixir.semantic.structure.definition.presentation")

        private fun computePresentation(definition: Definition): Structure {
            val parentPresentation = definition.enclosingModular.presentation as Parent
            val location = parentPresentation.locatedPresentableText
            val lastIndex = location.lastIndexOf('.')
            val parentLocation: String?
            val name: String

            if (lastIndex != -1) {
                parentLocation = location.substring(0, lastIndex)
                name = location.substring(lastIndex + 1, location.length)
            } else {
                parentLocation = null
                name = location
            }

            return Structure(parentLocation, name)
        }
    }
}
