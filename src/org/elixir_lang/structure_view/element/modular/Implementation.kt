package org.elixir_lang.structure_view.element.modular

import com.intellij.ide.util.treeView.smartTree.TreeElement
import com.intellij.navigation.ItemPresentation
import org.elixir_lang.psi.NamedElement
import org.elixir_lang.structure_view.element.modular.implementation.ForName

class Implementation(parent: Modular? = null, override val semantic: org.elixir_lang.semantic.Implementation) :
    Module(parent, semantic) {
    /**
     * The name of the [.navigationItem].
     *
     * @return the [NamedElement.getName] if [.navigationItem] is a [NamedElement]; otherwise,
     * `null`.
     */
    override fun getName(): String? = semantic.canonicalName

    override fun getChildren(): Array<TreeElement> =
        semantic
            .forNames
            .map { forName ->
                ForName(this, forName)
            }
            .toTypedArray()

    /**
     * Returns the presentation of the tree element.
     *
     * @return the element presentation.
     */
    override fun getPresentation(): ItemPresentation =
        org.elixir_lang.navigation.item_presentation.modular.Module(location(), semantic)
}
