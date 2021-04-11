package org.elixir_lang.psi

import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveState
import com.intellij.psi.impl.source.tree.CompositeElement
import com.intellij.psi.util.siblings
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.name.Function.QUOTE
import org.elixir_lang.psi.call.name.Module.KERNEL
import org.elixir_lang.psi.impl.call.macroChildCallSequence
import org.elixir_lang.psi.scope.CallDefinitionClause
import org.elixir_lang.psi.scope.WhileIn.whileIn

object QuoteMacro {
    fun treeWalkUp(quoteCall: Call, resolveState: ResolveState, keepProcessing: (PsiElement, ResolveState) -> Boolean): Boolean {
        var accumulatorKeepProcessing = true

        for (childCall in quoteCall.macroChildCallSequence().filter { !resolveState.hasBeenVisited(it) }) {
            accumulatorKeepProcessing = when {
                Import.`is`(childCall) -> {
                    val childResolveState = resolveState.put(CallDefinitionClause.IMPORT_CALL, childCall).putVisitedElement(childCall)

                    Import.callDefinitionClauseCallWhile(childCall, childResolveState, keepProcessing)
                }
                Unquote.`is`(childCall) -> {
                    val childResolveState = resolveState.putVisitedElement(childCall)

                    Unquote.treeWalkUp(childCall, childResolveState, keepProcessing)
                }
                Use.`is`(childCall) -> {
                    val childResolveState = resolveState.putVisitedElement(childCall)

                    Use.treeWalkUp(childCall, childResolveState, keepProcessing)
                }
                childCall.isCalling(KERNEL, "unless") -> {
                    childCall.doBlock?.let { doBlock ->
                        doBlock.stab?.stabBody?.firstChild?.siblings()?.filter { it.node is CompositeElement }?.let { unlessExpressions ->
                            whileIn(unlessExpressions) { unlessExpression ->
                                keepProcessing(unlessExpression, resolveState)
                            }
                        }
                    } ?: true
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
