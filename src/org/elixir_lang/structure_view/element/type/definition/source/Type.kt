package org.elixir_lang.structure_view.element.type.definition.source

import com.intellij.ide.util.treeView.smartTree.TreeElement
import com.intellij.navigation.ItemPresentation
import com.intellij.psi.NavigatablePsiElement
import org.elixir_lang.navigation.item_presentation.Parent
import org.elixir_lang.structure_view.element.Element
import org.elixir_lang.structure_view.element.modular.Modular

class Type(
    private val modular: Modular,
    private val semantic: org.elixir_lang.semantic.type.Definition
) : Element<NavigatablePsiElement>(semantic.psiElement as NavigatablePsiElement) {

    /**
     * No children.
     *
     * @return empty array.
     */
    override fun getChildren(): Array<TreeElement> = emptyArray()

    /**
     * Returns the presentation of the tree element.
     *
     * @return the element presentation.
     */
    override fun getPresentation(): ItemPresentation {
        val parentPresentation = modular.presentation as Parent
        val location = parentPresentation.locatedPresentableText

        return org.elixir_lang.navigation.item_presentation.Type(
            location,
            semantic
        )
    }
}
