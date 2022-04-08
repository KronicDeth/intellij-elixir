package org.elixir_lang.structure_view.element.call.definition.delegation

import com.intellij.ide.util.treeView.smartTree.TreeElement
import com.intellij.navigation.ItemPresentation
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiErrorElement
import org.elixir_lang.navigation.item_presentation.NameArityInterval
import org.elixir_lang.psi.*
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.operation.Normalized.operatorIndex
import org.elixir_lang.semantic.call.definition.clause.Visibility
import org.elixir_lang.semantic.call.definition.delegation.Head
import org.elixir_lang.semantic.semantic
import org.elixir_lang.structure_view.element.Element
import org.elixir_lang.structure_view.element.Presentable
import org.elixir_lang.structure_view.element.call.Definition

class Head(val callDefinition: Definition, val semantic: Head) :
    Element<Call>(semantic.call), Presentable {
    /**
     * Heads have no children since they have no body.
     *
     * @return empty array
     */
    override fun getChildren(): Array<TreeElement> = emptyArray()

    /**
     * Returns the presentation of the tree element.
     *
     * @return the element presentation.
     */
    override fun getPresentation(): ItemPresentation =
        org.elixir_lang.navigation.item_presentation.CallDefinitionHead(
            callDefinition.presentation as NameArityInterval,
            Visibility.PUBLIC,
            navigationItem
        )

    companion object {
        private fun argumentEnclosingDelegationCall(arguments: PsiElement): Call? =
            arguments
                .let { it as? ElixirNoParenthesesOneArgument }
                ?.parent?.let { it as? Call }
                ?.takeIf { argumentParent ->
                    argumentParent.semantic is org.elixir_lang.semantic.call.definition.Delegation
                }

        fun enclosingDelegationCall(call: Call): Call? {
            // reverse of {@link org.elixir_lang.structure_view.element.Delegation.filterCallDefinitionHeadCallList()}
            val parent = call.parent

            return if (parent is ElixirList) {
                val grandParent = parent.parent

                if (grandParent is ElixirAccessExpression) {
                    val greatGrandParent = grandParent.parent

                    argumentEnclosingDelegationCall(greatGrandParent)
                } else {
                    null
                }
            } else {
                argumentEnclosingDelegationCall(parent)
            }
        }

        fun `is`(call: Call): Boolean = call is UnqualifiedParenthesesCall<*>

        fun nameIdentifier(head: PsiElement): PsiElement? =
            if (head is ElixirMatchedAtOperation) {
                head.operator()
            } else {
                val stripped = org.elixir_lang.semantic.call.definition.Head.strip(head)

                if (stripped is Call) {
                    stripped.functionNameElement()
                } else {
                    stripped
                }
            }

        /**
         * The head without the guard clause
         *
         * @param head `name(arg, ...) when ...`
         * @return `name(arg, ...)`.  `head` if no guard clause.
         */
        tailrec fun stripGuard(head: PsiElement): PsiElement =
            if (head is ElixirMatchedWhenOperation) {
                val children = head.children

                val operatorIndex = operatorIndex(children)
                var onlyNonErrorIndex = -1

                for (i in 0 until operatorIndex) {
                    if (children[i] !is PsiErrorElement) {
                        if (onlyNonErrorIndex == -1) {
                            // first
                            onlyNonErrorIndex = i
                        } else {
                            // more than one
                            onlyNonErrorIndex = -1

                            break
                        }
                    }
                }

                if (onlyNonErrorIndex != -1) {
                    stripGuard(children[onlyNonErrorIndex])
                } else {
                    head
                }
            } else {
                head
            }
    }
}
