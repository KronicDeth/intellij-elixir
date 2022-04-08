package org.elixir_lang.structure_view.element

import com.intellij.ide.util.treeView.smartTree.TreeElement
import com.intellij.navigation.ItemPresentation
import org.elixir_lang.navigation.item_presentation.Parent
import org.elixir_lang.psi.call.Call
import org.elixir_lang.structure_view.element.call.definition.Clause
import org.elixir_lang.structure_view.element.modular.Modular
import org.elixir_lang.structure_view.element.modular.Module.Companion.modularChildren

/**
 * A `quote ... do ... end` block
 */
class Quote private constructor(val parent: Presentable?, val semantic: org.elixir_lang.semantic.quote.Call) :
    Element<Call>(semantic.call) {

    /**
     * Quote in top-level, outside of any Module
     */
    constructor(semantic: org.elixir_lang.semantic.quote.Call) : this(null, semantic)

    /**
     * Quote in body of `defmodule` or another `quote`.
     *
     * @param modular Direct parent module or quote of `call`
     */
    constructor(modular: Modular?, semantic: org.elixir_lang.semantic.quote.Call) :
            this(modular as Presentable, semantic)

    /**
     * Quote in body of `CallDefinitionClause` as is common is `defmacro __using__(_) do ... end`
     *
     * @param callDefinitionClause function definition clause in which `call` is.
     */
    constructor(callDefinitionClause: Clause, semantic: org.elixir_lang.semantic.quote.Call) :
            this(callDefinitionClause as Presentable, semantic)
    /*
     * Methods
     */
    /**
     * Returns the presentation of the tree element.
     *
     * @return the element presentation.
     */
    override fun getPresentation(): ItemPresentation {
        val location: String? = parent?.presentation?.let { it as? Parent }?.locatedPresentableText

        return org.elixir_lang.navigation.item_presentation.Quote(location, navigationItem)
    }

    /**
     * Returns the list of children of the tree element.
     *
     * @return the list of children.
     */
    override fun getChildren(): Array<TreeElement> =
        modular()
            ?.let { modular ->
                modularChildren(modular, semantic)
            }
            ?: emptyArray()

    fun modular(): Modular? =
        when (parent) {
            is Clause -> org.elixir_lang.structure_view.element.modular.Quote(parent)
            is Modular -> parent
            else -> null
        }

    /**
     * Returns a new [Quote] with the [.parent] sent to `module`, so that the location is resolved
     * correctly for `use <ALIAS>`.
     *
     * @param module
     * @return
     */
    fun used(use: Use?): Quote = Quote(org.elixir_lang.structure_view.element.modular.Use(use!!), semantic)
}
