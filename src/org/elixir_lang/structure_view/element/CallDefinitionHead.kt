package org.elixir_lang.structure_view.element

import com.intellij.ide.util.treeView.smartTree.TreeElement
import com.intellij.navigation.ItemPresentation
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiErrorElement
import com.intellij.psi.ResolveState
import org.elixir_lang.ArityRange
import org.elixir_lang.NameArityInterval
import org.elixir_lang.Visibility
import org.elixir_lang.navigation.item_presentation.NameArity
import org.elixir_lang.psi.*
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.impl.PsiNamedElementImpl.unquoteName
import org.elixir_lang.psi.impl.stripAccessExpression
import org.elixir_lang.psi.operation.Normalized.operatorIndex

class CallDefinitionHead(val callDefinition: CallDefinition, private val visibility: Visibility, call: Call) :
        Element<Call>(call), Presentable, Visible {
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
                    callDefinition.presentation as NameArity,
                    visibility,
                    navigationItem
            )

    /**
     * The visibility of the element.
     *
     * @return [Visibility.PUBLIC].
     */
    override fun visibility(): Visibility = visibility

    companion object {
        private fun argumentEnclosingDelegationCall(arguments: PsiElement): Call? =
                if (arguments is ElixirNoParenthesesOneArgument) {
                    (arguments.getParent() as? Call)?.let { argumentsParent ->
                        if (Delegation.`is`(argumentsParent)) {
                            argumentsParent
                        } else {
                            null
                        }
                    }
                } else {
                    null
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
                if (head is ElixirMatchedAtNonNumericOperation) {
                    val atNonNumericOperation = head as AtNonNumericOperation
                    atNonNumericOperation.operator()
                } else {
                    val stripped = strip(head)

                    if (stripped is Call) {
                        stripped.functionNameElement()
                    } else {
                        stripped
                    }
                }

        fun nameArityInterval(head: PsiElement, state: ResolveState): NameArityInterval? =
            if (head is ElixirMatchedAtNonNumericOperation) {
                val name = head.operator().text.trim { it <= ' ' }
                val arityInterval = ArityInterval(1, 1)

                NameArityInterval(name, arityInterval)
            } else {
                (strip(head) as? Call)?.let { stripped ->
                    val functionName = stripped.functionName()

                    if (functionName != null) {
                        val name = unquoteName(stripped, functionName)
                        val arityInterval = stripped.resolvedFinalArityInterval()

                        NameArityInterval(name, arityInterval).adjusted(state)
                    } else {
                        null
                    }
                }
            }

        /**
         * Head without parentheses around the guard or guarded head
         *
         * @param head `((((name(arg, ...))) when ...))`
         * @return `name(arg, ...)`
         */
        fun strip(head: PsiElement): PsiElement =
                stripAllOuterParentheses(head).let { stripGuard(it) }.let { stripAllOuterParentheses(it) }

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

        /**
         * Strips each set of outer parentheses from `head` until there aren't anymore.
         *
         * @param head `((value))`
         * @return `value`.  `head` if no outer parentheses
         */
        @JvmStatic
        fun stripAllOuterParentheses(head: PsiElement): PsiElement {
            var stripped = head
            var previousStripped: PsiElement

            do {
                previousStripped = stripped
                stripped = stripOuterParentheses(previousStripped)
            } while (previousStripped !== stripped)

            return stripped
        }

        /**
         * Strips outer parentheses from `head`.
         *
         * @param head `(value)`
         * @return `value`.  `head` if no outer parentheses
         */
        private fun stripOuterParentheses(head: PsiElement): PsiElement {
            val strippedHead = head.stripAccessExpression()

            return (strippedHead as? ElixirParentheticalStab)?.let {
                it.children.singleOrNull()?.let {
                    (it as? ElixirStab)?.let {
                        it.children.singleOrNull()?.let {
                            (it as? ElixirStabBody)?.children?.singleOrNull()
                        }
                    }
                }
            } ?: strippedHead
        }
    }
}
