package org.elixir_lang.psi.scope

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import org.elixir_lang.psi.CallDefinitionClause
import org.elixir_lang.psi.ElixirStabNoParenthesesSignature
import org.elixir_lang.psi.ElixirStabParenthesesSignature
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.operation.InMatch
import org.elixir_lang.psi.operation.Infix
import org.elixir_lang.psi.operation.Match

/**
 * Whether an identifier is the value of a match, and so a read, rather than part of a pattern that binds it. The
 * symbol model and the resolver have to agree on this, or a binding the one declares is a read the other skips.
 */
object MatchRead {
    /**
     * A read is the value of a match, so it sits on the right of the nearest one: `y = (x = 1)` still declares `x`.
     * Both sides of a match bind when the match is itself a pattern, so the walk carries on past a right operand
     * until it knows: a stab signature or a definition head means a pattern, and so does the left operand of an
     * enclosing match. The right of `<-` is the enumerable and reads whatever encloses it.
     */
    @JvmStatic
    fun `is`(element: PsiElement): Boolean {
        var child: PsiElement = element
        var onRight = false

        for (ancestor in generateSequence(element.parent) { it.parent }.takeWhile { it !is PsiFile }) {
            when (ancestor) {
                is ElixirStabNoParenthesesSignature, is ElixirStabParenthesesSignature -> return false
                is InMatch -> return !isLeftOperand(ancestor, child)
                is Match -> {
                    if (isLeftOperand(ancestor, child)) return false
                    onRight = true
                }
                // inside the head of a definition is a pattern; a clause still missing its head reads
                is Call ->
                    if (CallDefinitionClause.`is`(ancestor)) {
                        return onRight &&
                            CallDefinitionClause.head(ancestor)
                                ?.let { PsiTreeUtil.isAncestor(it, element, false) } != true
                    }
            }
            child = ancestor
        }

        return onRight
    }

    private fun isLeftOperand(operation: Infix, child: PsiElement): Boolean =
        operation.leftOperand()?.let { left -> PsiTreeUtil.isAncestor(left, child, false) } == true
}
