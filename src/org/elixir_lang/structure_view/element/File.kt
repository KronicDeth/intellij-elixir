package org.elixir_lang.structure_view.element

import com.intellij.ide.util.treeView.smartTree.TreeElement
import com.intellij.navigation.ItemPresentation
import com.intellij.psi.util.PsiTreeUtil
import org.elixir_lang.psi.ElixirFile
import org.elixir_lang.psi.call.Call
import org.elixir_lang.semantic.semantic
import org.elixir_lang.structure_view.element.modular.Implementation
import org.elixir_lang.structure_view.element.modular.Module
import org.elixir_lang.structure_view.element.modular.Protocol
import org.elixir_lang.structure_view.element.modular.Unknown
import org.elixir_lang.structure_view.element.modular.Unknown.Companion.`is`

class File  /*
     * Constructors
     */
    (file: ElixirFile?) : Element<ElixirFile?>(file) {
    /*
     * Public Instance Methods
     */
    override fun getChildren(): Array<TreeElement> =
        PsiTreeUtil
            .getChildrenOfType(navigationItem, Call::class.java)
            ?.mapNotNull { call ->
                when (val semantic = call.semantic) {
                    is org.elixir_lang.semantic.implementation.Call -> Implementation(semantic = semantic)
                    is org.elixir_lang.semantic.Module -> Module(semantic = semantic)
                    is org.elixir_lang.semantic.protocol.Call -> Protocol(semantic = semantic)
                    is org.elixir_lang.semantic.quote.Call -> Quote(semantic)
                    else -> if (`is`(call)) {
                        Unknown(null, call)
                    } else {
                        null
                    }
                }
            }
            ?.toTypedArray()
            ?: emptyArray()

    /**
     * Returns the presentation of the tree element.
     *
     * @return the element presentation.
     */
    override fun getPresentation(): ItemPresentation {
        return navigationItem!!.presentation!!
    }
}
