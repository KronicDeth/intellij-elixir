package org.elixir_lang.structure_view.element.modular.implementation

import com.intellij.ide.util.treeView.smartTree.TreeElement
import com.intellij.navigation.ItemPresentation
import com.intellij.psi.NavigatablePsiElement
import org.elixir_lang.navigation.item_presentation.implementation.ForName
import org.elixir_lang.psi.NamedElement
import org.elixir_lang.structure_view.element.Element

class ForName(
    val parent: org.elixir_lang.structure_view.element.modular.Implementation,
    val semantic: org.elixir_lang.semantic.implementation.ForName
) :
    Element<NavigatablePsiElement>(semantic.psiElement as NavigatablePsiElement) {
    /**
     * The name of the [.navigationItem].
     *
     * @return the [NamedElement.getName] if [.navigationItem] is a [NamedElement]; otherwise,
     * `null`.
     */
    override fun getName(): String? = semantic.canonicalName

    /**
     * Returns the presentation of the tree element.
     *
     * @return the element presentation.
     */
    override fun getPresentation(): ItemPresentation =
        ForName(
            semantic.protocolName,
            semantic.forName
        )

    override fun getChildren(): Array<TreeElement> = emptyArray()
}
