package org.elixir_lang.psi

import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveState
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.name.Function.UNLESS
import org.elixir_lang.psi.call.name.Module.KERNEL
import org.elixir_lang.psi.impl.call.whileInStabBodyChildExpressions
import org.elixir_lang.psi.impl.childExpressions
import org.elixir_lang.psi.scope.WhileIn.whileIn

object Unless {
    /**
     * Whether `call` is an
     *
     * ```
     * unless ... do
     * ...
     * end
     * ```
     */
    fun `is`(call: Call): Boolean = call.isCalling(KERNEL, UNLESS, 2)

    /**
     * Calls `keepProcessing` on the false and true (`else`) branches of the unless
     */
    fun treeWalkUp(unlessCall: Call,
                   resolveState: ResolveState,
                   keepProcessing: (PsiElement, ResolveState) -> Boolean): Boolean {
        val unlessCallResolveState = resolveState.putVisitedElement(unlessCall)

        // because this doesn't actually check which branch would be called based on the condition both
        // branches are ALWAYS walked
        val foundInFalseBranch = treeWalkUpFalseBranch(unlessCall, unlessCallResolveState, keepProcessing)
        val foundInTrueBranch = treeWalkUpTrueBranch(unlessCall, unlessCallResolveState, keepProcessing)

        return foundInFalseBranch || foundInTrueBranch
    }

    private fun treeWalkUpFalseBranch(unlessCall: Call,
                                      resolveState: ResolveState,
                                      keepProcessing: (PsiElement, ResolveState) -> Boolean): Boolean =
            unlessCall.whileInStabBodyChildExpressions { stabBodyChildExpression ->
                keepProcessing(stabBodyChildExpression, resolveState)
            }

    private fun treeWalkUpTrueBranch(unlessCall: Call,
                                     resolveState: ResolveState,
                                     keepProcessing: (PsiElement, ResolveState) -> Boolean):  Boolean =
            unlessCall
                    .doBlock
                    ?.blockList
                    ?.blockItemList
                    ?.firstOrNull { blockItem ->
                        blockItem.blockIdentifier.textMatches("else")
                    }
                    ?.stab
                    ?.stabBody
                    ?.childExpressions()
                    ?.let { elseChildExpressions ->
                        whileIn(elseChildExpressions) { elseChildExpression ->
                            keepProcessing(elseChildExpression, resolveState)
                        }
                    }
                    ?: true
}
