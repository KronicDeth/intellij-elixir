package org.elixir_lang.structure_view.element.type.definition.source

import com.intellij.ide.util.treeView.smartTree.TreeElement
import com.intellij.navigation.ItemPresentation
import com.intellij.psi.NavigatablePsiElement
import org.elixir_lang.navigation.item_presentation.Parent
import org.elixir_lang.semantic.call.definition.clause.Time
import org.elixir_lang.semantic.type.definition.source.Specification
import org.elixir_lang.structure_view.element.Element
import org.elixir_lang.structure_view.element.modular.Modular

/**
 * A call definition @spec
 */
class Specification(
    private val modular: Modular,
    private val semantic: Specification,
    private val callback: Boolean,
    private val time: Time
) : Element<NavigatablePsiElement>(semantic.psiElement as NavigatablePsiElement) {
    /**
     * No children.
     *
     * @return empty array
     */
    override fun getChildren(): Array<TreeElement> = emptyArray()

    /**
     * Returns the presentation of the tree element.
     *
     * @return the element presentation.
     */
    override fun getPresentation(): ItemPresentation {
        val location = (modular.presentation as Parent).locatedPresentableText

        return org.elixir_lang.navigation.item_presentation.CallDefinitionSpecification(
            location,
            navigationItem,
            callback,
            time
        )
    }
}
