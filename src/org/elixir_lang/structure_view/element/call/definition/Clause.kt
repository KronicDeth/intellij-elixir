package org.elixir_lang.structure_view.element.call.definition

import com.intellij.ide.util.treeView.smartTree.TreeElement
import com.intellij.navigation.ItemPresentation
import com.intellij.psi.PsiElement
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.impl.call.macroChildCalls
import org.elixir_lang.semantic.call.definition.Clause
import org.elixir_lang.semantic.semantic
import org.elixir_lang.structure_view.element.Element
import org.elixir_lang.structure_view.element.Presentable
import org.elixir_lang.structure_view.element.Quote
import org.elixir_lang.structure_view.element.call.Definition
import org.elixir_lang.structure_view.element.modular.*
import org.jetbrains.annotations.Contract
import java.util.*

/**
 * Constructs a clause for `callDefinition`.
 *
 * @param callDefinition holds all sibling clauses for `call` for the same name, arity. and time
 * @param call           a def(macro)?p? call
 */
class Clause(val callDefinition: Definition, val semantic: Clause) :
    Element<Call>(semantic.psiElement as Call), Presentable {
    /*
     * Public Instance Methods
     */

    override fun getChildren(): Array<TreeElement> =
        navigationItem.macroChildCalls().let {
            childCallTreeElements(it)
        } ?: emptyArray()

    /**
     * Returns the presentation of the tree element.
     *
     * @return the element presentation.
     */
    override fun getPresentation(): ItemPresentation = semantic.presentation

    private fun addChildCall(treeElementList: MutableList<TreeElement>, childCall: Call) {
        when (val semantic = childCall.semantic) {
            is org.elixir_lang.semantic.implementation.Call -> Implementation(callDefinition.modular, semantic)
            is org.elixir_lang.semantic.Module -> Module(callDefinition.modular, semantic)
            is org.elixir_lang.semantic.quote.Call -> Quote(this, semantic)
            else -> null
        }?.run {
            treeElementList.add(this)
        }
    }

    @Contract(pure = true)
    private fun childCallTreeElements(childCalls: Array<Call>?): Array<TreeElement>? =
        childCalls?.let {
            val treeElementList = ArrayList<TreeElement>(it.size)

            for (childCall in it) {
                addChildCall(treeElementList, childCall)
            }

            treeElementList.toTypedArray()
        }

    /**
     * Whether this clause would match the given arguments and be called.
     *
     * @param arguments argument being passed to this clauses' function.
     * @return `true` if arguments matches up-to the available information about the arguments; otherwise,
     * `false`
     */
    fun isMatch(arguments: Array<PsiElement>): Boolean = false
}

