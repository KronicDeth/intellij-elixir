package org.elixir_lang.structure_view.element.ex_unit.case

import com.intellij.ide.util.treeView.smartTree.TreeElement
import com.intellij.navigation.ItemPresentation
import org.elixir_lang.psi.call.Call
import org.elixir_lang.structure_view.element.Element
import org.elixir_lang.navigation.item_presentation.ex_unit.case.Describe

class Describe(call: Call) : Element<Call>(call) {
    override fun getPresentation(): ItemPresentation = Describe(navigationItem)
    override fun getChildren(): Array<TreeElement> = emptyArray()
}
