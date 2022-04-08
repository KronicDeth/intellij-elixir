package org.elixir_lang.structure_view.element

import com.intellij.ide.structureView.StructureViewTreeElement
import com.intellij.ide.util.treeView.smartTree.TreeElement
import com.intellij.navigation.ItemPresentation
import com.intellij.navigation.NavigationItem
import org.elixir_lang.navigation.item_presentation.NameArityInterval
import org.elixir_lang.navigation.item_presentation.Parent
import org.elixir_lang.semantic.call.definition.clause.Time
import org.elixir_lang.semantic.call.definition.eex.FunctionFrom
import org.elixir_lang.structure_view.element.modular.Modular

class EExFunctionFrom(val modular: Modular, val semantic: FunctionFrom) :
    StructureViewTreeElement, NavigationItem {
    override fun navigate(requestFocus: Boolean) {
        if (canNavigate()) {
            semantic.call.navigate(requestFocus)
        }
    }

    override fun canNavigate(): Boolean = true
    override fun canNavigateToSource(): Boolean = true

    override fun getName(): String? = semantic.nameArityInterval?.toString()

    override fun getValue(): Any = semantic.call

    override fun getPresentation(): ItemPresentation {
        val parent = modular.presentation as Parent
        val location = parent.locatedPresentableText

        return NameArityInterval(
            location,
            false,
            Time.RUN,
            visibility = semantic.visibility,
            overridable = false,
            override = false,
            nameArityInterval = semantic.nameArityInterval!!
        )
    }

    override fun getChildren(): Array<TreeElement> = arrayOf(EExFunctionFromHead(this))
}
