package org.elixir_lang.psi.scope.variable

import com.intellij.openapi.progress.ProgressManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.concurrency.annotations.RequiresReadLock
import org.elixir_lang.psi.CallDefinitionClause
import org.elixir_lang.psi.ElixirAnonymousFunction
import org.elixir_lang.psi.ElixirDoBlock
import org.elixir_lang.psi.ElixirStab
import org.elixir_lang.psi.ElixirStabBody
import org.elixir_lang.psi.ElixirStabNoParenthesesSignature
import org.elixir_lang.psi.ElixirStabParenthesesSignature
import org.elixir_lang.psi.UnaryOperation
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.name.Function
import org.elixir_lang.psi.call.name.Module
import org.elixir_lang.psi.operation.InMatch
import org.elixir_lang.psi.operation.Match
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
                is PsiFile, is ElixirDoBlock -> return null
                // an `fn` still missing its `->` parses its parameters as a body with no signature
                is ElixirStabBody -> return headlessFunction(ancestor)
                is When -> if (ancestor.rightOperand().holds(element)) return null
                is UnaryOperation ->
                    if (ancestor.operator().text == "^" && ancestor.operand().holds(element)) return null
                // a `cond` clause's head is a condition, read like any expression
                is ElixirStabNoParenthesesSignature, is ElixirStabParenthesesSignature ->
                    return ancestor.takeUnless { isCondClauseHead(it) }
                is ElixirAnonymousFunction -> return ancestor
                is InMatch -> ancestor.leftOperand()?.takeIf { it.holds(element) }?.let { return it }
                is Call -> if (CallDefinitionClause.`is`(ancestor)) {
                    return CallDefinitionClause.head(ancestor)?.takeIf { it.holds(element) }
                }
            }
        }

        return null
    }

    /**
     * Whether [element] is the value of a match, and so a read: it sits on the right of the nearest match, and that
     * match is not itself inside a pattern, where both sides bind: `%{a: [_ | _] = x} = map` binds `x`.
     */
    @RequiresReadLock
    fun isMatchRead(element: PsiElement): Boolean {
        var child: PsiElement = element
        var onRight = false

        for (ancestor in generateSequence(element.parent) { it.parent }) {
            ProgressManager.checkCanceled()
            when (ancestor) {
                is PsiFile -> return onRight
                // a body inside a match's value reads: `f = fn -> x end`
                is Match -> if (ancestor.leftOperand().holds(child)) return false else onRight = true
                is InMatch -> return !ancestor.leftOperand().holds(child)
                // a clause pattern binds both sides of a match inside it; a `cond` head is a condition, so it reads
                is ElixirStabNoParenthesesSignature, is ElixirStabParenthesesSignature ->
                    return isCondClauseHead(ancestor)
                // a definition binds in its head and reads in its body, whatever match the definition sits in
                is Call -> if (CallDefinitionClause.`is`(ancestor)) {
                    return !CallDefinitionClause.head(ancestor).holds(element)
                }
            }
            child = ancestor
        }

        return onRight
    }

    /** Whether [element] sits in a pattern's `when` guard, where a name is read. */
    fun isGuardRead(element: PsiElement): Boolean =
        generateSequence(element.parent) { it.parent }
            .takeWhile { it !is PsiFile && it !is ElixirStabBody && it !is ElixirDoBlock }
            .filterIsInstance<When>()
            .any { it.rightOperand().holds(element) }

    private fun PsiElement?.holds(element: PsiElement): Boolean =
        this != null && PsiTreeUtil.isAncestor(this, element, false)

    private fun headlessFunction(body: ElixirStabBody): ElixirAnonymousFunction? =
        (body.parent as? ElixirStab)?.parent as? ElixirAnonymousFunction

    /** The clause's own call is the first one above its stab; a stab in a body or an `fn` belongs to no call. */
    private fun isCondClauseHead(signature: PsiElement): Boolean =
        generateSequence(signature.parent) { it.parent }
            .takeWhile { it !is PsiFile && it !is ElixirStabBody && it !is ElixirAnonymousFunction }
            .filterIsInstance<Call>()
            .firstOrNull()
            ?.isCallingMacro(Module.KERNEL, Function.COND) == true
}
