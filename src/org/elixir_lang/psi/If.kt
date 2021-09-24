package org.elixir_lang.psi

import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveState
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.name.Function.IF
import org.elixir_lang.psi.call.name.Module.KERNEL
import org.elixir_lang.psi.impl.call.whileInStabBodyChildExpressions
import org.elixir_lang.psi.impl.childExpressions
import org.elixir_lang.psi.scope.WhileIn.whileIn

object If {
    /**
     * Whether `call` is an
     *
     * ```
     * if ... do
     * ...
     * end
     * ```
     */
    fun `is`(call: Call): Boolean = call.isCalling(KERNEL, IF, 2)

    /**
     * Calls `keepProcessing` on the true and false (`else`) branches of the if
     */
    fun treeWalkUp(ifCall: Call,
                   resolveState: ResolveState,
                   keepProcessing: (PsiElement, ResolveState) -> Boolean): Boolean {
        val ifCallResolveState = resolveState.putVisitedElement(ifCall)

        // because this doesn't actually check which branch would be called based on the condition both
        // branches are ALWAYS walked
        val foundInTrueBranch = treeWalkUpTrueBranch(ifCall, ifCallResolveState, keepProcessing)
        val foundInFalseBranch = treeWalkUpFalseBranch(ifCall, ifCallResolveState, keepProcessing)

        return foundInTrueBranch || foundInFalseBranch
    }

    private fun treeWalkUpTrueBranch(ifCall: Call,
                                     resolveState: ResolveState,
                                     keepProcessing: (PsiElement, ResolveState) -> Boolean): Boolean =
            ifCall.whileInStabBodyChildExpressions { stabBodyChildExpression ->
                keepProcessing(stabBodyChildExpression, resolveState)
            }

    private fun treeWalkUpFalseBranch(ifCall: Call,
    resolveState: ResolveState,
    keepProcessing: (PsiElement, ResolveState) -> Boolean):  Boolean =
            ifCall
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
