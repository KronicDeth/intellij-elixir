package org.elixir_lang.semantic.call.definition

import com.intellij.psi.PsiElement
import org.elixir_lang.psi.ElixirParentheticalStab
import org.elixir_lang.psi.ElixirStab
import org.elixir_lang.psi.ElixirStabBody
import org.elixir_lang.psi.impl.stripAccessExpression
import org.elixir_lang.structure_view.element.call.definition.delegation.Head

object Head {
    /**
     * Head without parentheses around the guard or guarded head
     *
     * @param head `((((name(arg, ...))) when ...))`
     * @return `name(arg, ...)`
     */
    fun strip(head: PsiElement): PsiElement =
        stripAllOuterParentheses(head).let { Head.stripGuard(it) }.let { stripAllOuterParentheses(it) }

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
