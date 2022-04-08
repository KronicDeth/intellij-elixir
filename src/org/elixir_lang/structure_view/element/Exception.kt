package org.elixir_lang.structure_view.element

import com.intellij.ide.util.treeView.smartTree.TreeElement
import com.intellij.navigation.ItemPresentation
import org.elixir_lang.navigation.item_presentation.Exception
import org.elixir_lang.navigation.item_presentation.Parent
import org.elixir_lang.psi.call.Call
import org.elixir_lang.semantic.call.definition.clause.Time
import org.elixir_lang.structure_view.element.call.Definition
import org.elixir_lang.structure_view.element.modular.Modular
import org.jetbrains.annotations.Contract

/**
 * A `defexception` with its fields and the callbacks `exception/1` and `message/1` if overridden.
 */
class Exception(private val modular: Modular, private val semantic: org.elixir_lang.semantic.Exception) :
    Element<Call>(semantic.call) {
    private var callbacks: MutableList<Definition>? = null

    /**
     * Adds callback function
     *
     * @param callback the callback function: either exception/1 or message/1.
     */
    @Contract(pure = false)
    fun callback(callback: Definition) {
        assert(callback.semantic.nameArityInterval?.arityInterval?.contains(1) == true)
        assert(callback.time === Time.RUN)
        val callbackName = callback.name()
        assert(callbackName == "exception" || callbackName == "message")
        if (callbacks == null) {
            callbacks = ArrayList()
        }
        callbacks!!.add(callback)
    }

    /**
     * Returns the list of children of the tree element.
     *
     * @return the list of children.
     */
    override fun getChildren(): Array<TreeElement> {
        val childList: MutableList<TreeElement> = ArrayList()
        childList.add(
            Structure(semantic.structure)
        )
        if (callbacks != null) {
            childList.addAll(callbacks!!)
        }
        return childList.toTypedArray()
    }

    /**
     * Returns the presentation of the tree element.
     *
     * @return the element presentation.
     */
    override fun getPresentation(): ItemPresentation {
        val parentPresentation = modular.presentation as Parent
        val location = parentPresentation.locatedPresentableText
        val lastIndex = location.lastIndexOf('.')
        val parentLocation: String?
        val name: String
        if (lastIndex != -1) {
            parentLocation = location.substring(0, lastIndex)
            name = location.substring(lastIndex + 1, location.length)
        } else {
            parentLocation = null
            name = location
        }
        return Exception(
            parentLocation,
            name
        )
    }
}
