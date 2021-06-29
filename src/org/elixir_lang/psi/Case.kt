package org.elixir_lang.psi

import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveState
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.name.Function.QUOTE
import org.elixir_lang.psi.call.name.Module.KERNEL
import org.elixir_lang.psi.impl.childExpressions
import org.elixir_lang.psi.scope.WhileIn.whileIn

object Case {
    fun treeWalkUp(caseCall: Call,
                   state: ResolveState,
                   keepProcessing: (PsiElement, ResolveState) -> Boolean): Boolean {
        val lastChildCallSequence =
                caseCall
                        .doBlock
                        ?.stab
                        ?.stabOperationList
                        ?.asSequence()
                        ?.mapNotNull(ElixirStabOperation::getStabBody)
                        ?.mapNotNull { stabBody ->
                            stabBody
                                    .childExpressions(forward = false)
                                    .filterIsInstance<Call>()
                                    .firstOrNull()
                                    ?.takeUnlessHasBeenVisited(state)
                        }
                        .orEmpty()

        return whileIn(lastChildCallSequence) { lastChildCall ->
            treeWalkUpFromLastChildCall(lastChildCall, state, keepProcessing)
        }
    }

    fun treeWalkUpFromLastChildCall(lastChildCall: Call, state: ResolveState, keepProcessing: (PsiElement, ResolveState) -> Boolean): Boolean {
        val resolveModuleName = lastChildCall.resolvedModuleName()
        val functionName = lastChildCall.functionName()

        return if (resolveModuleName != null && functionName != null) {
            when {
                resolveModuleName == KERNEL && functionName == QUOTE -> {
                    val lastChildCallResolveState = state.putVisitedElement(lastChildCall)

                    QuoteMacro.treeWalkUp(lastChildCall, lastChildCallResolveState, keepProcessing)
                }
                else -> true
            }
        } else {
            true
        }
    }
}
