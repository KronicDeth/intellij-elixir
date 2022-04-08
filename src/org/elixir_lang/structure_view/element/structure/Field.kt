package org.elixir_lang.structure_view.element.structure

import com.intellij.ide.util.treeView.smartTree.TreeElement
import com.intellij.navigation.ItemPresentation
import com.intellij.psi.NavigatablePsiElement
import org.elixir_lang.structure_view.element.Element

/**
 * A `defstruct` field only given by its name
 */
class Field(
    val semantic: org.elixir_lang.semantic.structure.definition.Field
) : Element<NavigatablePsiElement>(semantic.psiElement as NavigatablePsiElement) {
    override fun getChildren(): Array<TreeElement> = emptyArray()
    override fun getPresentation(): ItemPresentation = semantic.presentation
}
