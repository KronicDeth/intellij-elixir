package org.elixir_lang.psi

import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveState
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.name.Function.*
import org.elixir_lang.psi.call.name.Module.KERNEL
import org.elixir_lang.psi.impl.call.macroChildCallSequence
import org.elixir_lang.psi.impl.call.whileInStabBodyChildExpressions
import org.elixir_lang.psi.scope.CallDefinitionClause

object QuoteMacro {
    fun treeWalkUp(quoteCall: Call, resolveState: ResolveState, keepProcessing: (PsiElement, ResolveState) -> Boolean): Boolean =
            if (!resolveState.containsAncestorUnquote(quoteCall)) {
                quoteCall
                        .macroChildCallSequence()
                        .filter { !resolveState.hasBeenVisited(it) }
                        .let { treeWalkUp(it, resolveState, keepProcessing) }
            } else {
                true
            }

    fun treeWalkUp(childCallSequence: Sequence<Call>,
                   resolveState: ResolveState,
                   keepProcessing: (PsiElement, ResolveState) -> Boolean): Boolean {
        var accumulatorKeepProcessing = true

        for (childCall in childCallSequence) {
            accumulatorKeepProcessing = when {
                If.`is`(childCall) -> {
                    val childResolveState = resolveState.putVisitedElement(childCall)

                    If.treeWalkUp(childCall, childResolveState, keepProcessing)
                }
                Import.`is`(childCall) -> {
                    val childResolveState =
                            resolveState
                                    .put(CallDefinitionClause.IMPORT_CALL, childCall)
                                    .putVisitedElement(childCall)

                    Import.treeWalkUp(childCall,childResolveState, keepProcessing)
                }
                Unless.`is`(childCall) -> {
                    val childResolveState = resolveState.putVisitedElement(childCall)

                    Unless.treeWalkUp(childCall, childResolveState, keepProcessing)
                }
                Unquote.`is`(childCall) -> {
                    val childResolveState = resolveState.putVisitedElement(childCall)

                    Unquote.treeWalkUp(childCall, childResolveState, keepProcessing)
                }
                Use.`is`(childCall) -> {
                    val childResolveState = resolveState.putVisitedElement(childCall)

                    Use.treeWalkUp(childCall, childResolveState, keepProcessing)
                }
                childCall.isCalling(KERNEL, TRY) -> {
                    childCall.whileInStabBodyChildExpressions { grandChildExpression ->
                        keepProcessing(grandChildExpression, resolveState)
                    }
                }
                else -> keepProcessing(childCall, resolveState)
            }

            if (!accumulatorKeepProcessing) {
                break
            }
        }

        return accumulatorKeepProcessing
    }

    @JvmStatic
    fun `is`(call: Call): Boolean {
        // TODO change Elixir.Kernel to Elixir.Kernel.SpecialForms when resolving works
        return call.isCallingMacro(KERNEL, QUOTE, 1) || // without keyword arguments
                call.isCallingMacro(KERNEL, QUOTE, 2) // with keyword arguments
    }

}
