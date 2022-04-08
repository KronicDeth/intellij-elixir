package org.elixir_lang.structure_view.element

import com.intellij.ide.util.treeView.smartTree.TreeElement
import com.intellij.navigation.ItemPresentation
import org.elixir_lang.navigation.item_presentation.NameArityInterval
import org.elixir_lang.psi.call.Call
import org.elixir_lang.semantic.call.definition.clause.Visibility

class EExFunctionFromHead(private val eexFunctionFrom: EExFunctionFrom) : Element<Call>(eexFunctionFrom.semantic.call),
                                                                          Presentable {
    override fun getPresentation(): ItemPresentation =
        org.elixir_lang.navigation.item_presentation.CallDefinitionHead(
            eexFunctionFrom.presentation as NameArityInterval,
            Visibility.PUBLIC,
            navigationItem
        )

    override fun getChildren(): Array<TreeElement> = emptyArray()
}
