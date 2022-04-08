package org.elixir_lang.semantic.call.definition

import com.intellij.navigation.ItemPresentation
import com.intellij.psi.ElementDescriptionLocation
import org.elixir_lang.NameArityInterval
import org.elixir_lang.Presentable
import org.elixir_lang.semantic.Named
import org.elixir_lang.semantic.call.Definition
import org.elixir_lang.semantic.modular.Enclosed

interface Clause : Named, Enclosed, Presentable {
    val definition: Definition?
    val compiled: Boolean
    override val name: String?
        get() = nameArityInterval?.name
    val nameArityInterval: NameArityInterval?

    override fun elementDescription(location: ElementDescriptionLocation): String? =
        definition?.elementDescription(location)

    override val presentation: ItemPresentation
        get() = Presentable.getPresentation(psiElement) {
            computePresentation(this)
        }

    companion object {
        private fun computePresentation(clause: Clause): ItemPresentation {
            val definition = clause.definition!!

            return org.elixir_lang.navigation.item_presentation.CallDefinitionHead(
                definition.presentation,
                definition.visibility,
                clause.psiElement
            )
        }
    }
}
