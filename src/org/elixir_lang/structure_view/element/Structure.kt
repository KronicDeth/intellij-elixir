package org.elixir_lang.structure_view.element

import com.intellij.ide.util.treeView.smartTree.TreeElement
import com.intellij.navigation.ItemPresentation
import org.elixir_lang.psi.call.Call
import org.elixir_lang.semantic.structure.Definition

class Structure(val semantic: Definition) : Element<Call>(semantic.call) {
    override fun getChildren(): Array<TreeElement> =
        semantic
            .fields
            .map { org.elixir_lang.structure_view.element.structure.Field(it) }
            .toTypedArray()

    override fun getPresentation(): ItemPresentation = semantic.presentation
}
