package org.elixir_lang.structure_view.element.modular

import com.intellij.navigation.ItemPresentation
import org.elixir_lang.navigation.item_presentation.modular.Protocol

/**
 * A protocol definition
 */
class Protocol(parent: Modular? = null, override val semantic: org.elixir_lang.semantic.Protocol) :
    Module(parent, semantic) {
    /**
     * Returns the presentation of the tree element.
     *
     * @return the element presentation.
     */
    override fun getPresentation(): ItemPresentation = Protocol(location(), semantic)
}
