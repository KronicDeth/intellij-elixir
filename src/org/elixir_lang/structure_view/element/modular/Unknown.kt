package org.elixir_lang.structure_view.element.modular

import com.intellij.ide.util.treeView.smartTree.TreeElement
import com.intellij.navigation.ItemPresentation
import org.elixir_lang.navigation.item_presentation.modular.Unknown
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.impl.locationString
import org.elixir_lang.structure_view.element.Element

class Unknown(private val parent: Modular?, val call: Call) : Element<Call>(call) {
    override fun getPresentation(): ItemPresentation = Unknown(location, navigationItem)

    val location: String by lazy {
        Module.location(parent) ?: call.locationString()
    }

    override fun getChildren(): Array<TreeElement> {
        TODO()
    }

    companion object {
        @JvmStatic
        fun `is`(call: Call): Boolean = call.hasDoBlockOrKeyword()
    }
}
