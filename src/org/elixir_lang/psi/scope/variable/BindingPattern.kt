package org.elixir_lang.psi.scope.variable

import com.intellij.openapi.progress.ProgressManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.concurrency.annotations.RequiresReadLock
import org.elixir_lang.psi.CallDefinitionClause
import org.elixir_lang.psi.ElixirDoBlock
import org.elixir_lang.psi.ElixirStabBody
import org.elixir_lang.psi.ElixirStabNoParenthesesSignature
import org.elixir_lang.psi.ElixirStabParenthesesSignature
import org.elixir_lang.psi.UnaryOperation
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.name.Function
import org.elixir_lang.psi.call.name.Module
import org.elixir_lang.psi.operation.InMatch
import org.elixir_lang.psi.operation.When

/**
 * A pattern binds every name written in it afresh: a clause signature, a definition head, or the left of `<-` and
 * `\\`. What a pattern's `when` guard writes, and what it pins with `^`, is read, not bound.
 */
object BindingPattern {
    /** The pattern that binds [element] afresh, or `null` when [element] is a read or an assignment. */
    @RequiresReadLock
    fun of(element: PsiElement): PsiElement? {
        for (ancestor in generateSequence(element.parent) { it.parent }) {
            ProgressManager.checkCanceled()
            when (ancestor) {
                is PsiFile, is ElixirStabBody, is ElixirDoBlock -> return null
                is When -> if (ancestor.rightOperand().holds(element)) return null
                is UnaryOperation -> if (ancestor.operator().text == "^" && ancestor.operand().holds(element)) return null
                // a `cond` clause's head is a condition, read like any expression
                is ElixirStabNoParenthesesSignature, is ElixirStabParenthesesSignature ->
                    return ancestor.takeUnless { isCondClauseHead(it) }
                is InMatch -> ancestor.leftOperand()?.takeIf { it.holds(element) }?.let { return it }
                is Call -> if (CallDefinitionClause.`is`(ancestor)) {
                    return CallDefinitionClause.head(ancestor)?.takeIf { it.holds(element) }
                }
            }
        }

        return null
    }

    /** Whether [element] sits in a pattern's `when` guard, where a name is read. */
    fun isGuardRead(element: PsiElement): Boolean =
        generateSequence(element.parent) { it.parent }
            .takeWhile { it !is PsiFile && it !is ElixirStabBody && it !is ElixirDoBlock }
            .filterIsInstance<When>()
            .any { it.rightOperand().holds(element) }

    private fun PsiElement?.holds(element: PsiElement): Boolean =
        this != null && PsiTreeUtil.isAncestor(this, element, false)

    private fun isCondClauseHead(signature: PsiElement): Boolean =
        generateSequence(signature.parent) { it.parent }
            .takeWhile { it !is PsiFile }
            .filterIsInstance<Call>()
            .firstOrNull()
            ?.isCallingMacro(Module.KERNEL, Function.COND) == true
}
