package org.elixir_lang.structure_view.element.modular

import com.intellij.ide.util.treeView.smartTree.TreeElement
import com.intellij.navigation.ItemPresentation
import com.intellij.psi.*
import org.elixir_lang.navigation.item_presentation.Parent
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.impl.stripAccessExpression
import org.elixir_lang.structure_view.element.*
import org.elixir_lang.structure_view.element.call.Definition
import org.elixir_lang.structure_view.element.type.definition.source.Type
import java.util.*

/**
 *
 * @param parent the parent [Module] or [org.elixir_lang.structure_view.element.Quote] that scopes
 * `call`.
 * @param call the `Kernel.defmodule/2` call nested in `parent`.
 */
open class Module(
    protected val parent: Modular? = null,
    protected open val semantic: org.elixir_lang.semantic.Modular
) :
    Element<NavigatablePsiElement>(semantic.psiElement as NavigatablePsiElement), Modular {

    override fun getChildren(): Array<TreeElement> = modularChildren(this, semantic)

    /**
     * Returns the presentation of the tree element.
     *
     * @return the element presentation.
     */
    override fun getPresentation(): ItemPresentation =
        org.elixir_lang.navigation.item_presentation.modular.Module(location(), semantic)

    protected fun location(): String = location(parent) ?: semantic.locationString

    companion object {
        fun location(parent: Modular?): String? =
            parent?.presentation.let { it as? Parent }?.locatedPresentableText

        fun modularChildren(
            structural: Modular,
            semantic: org.elixir_lang.semantic.Modular
        ): Array<TreeElement> {
            val treeElements: MutableList<TreeElement> = mutableListOf()

            semantic.types.forEach { type ->
                treeElements.add(Type(structural, type))
            }
            semantic.callDefinitions.forEach { callDefinition ->
                treeElements.add(Definition(structural, callDefinition))
            }

            return treeElements.toTypedArray()
        }

        @JvmStatic
        fun nameIdentifier(call: Call): PsiElement? = call.primaryArguments()?.firstOrNull()?.stripAccessExpression()
    }
}

private inline fun Definition.also(block: (Definition) -> Unit): Definition {
    block(this)
    return this
}

