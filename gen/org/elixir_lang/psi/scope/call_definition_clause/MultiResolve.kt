package org.elixir_lang.psi.scope.call_definition_clause

import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementResolveResult
import com.intellij.psi.ResolveResult
import com.intellij.psi.ResolveState
import com.intellij.psi.util.PsiTreeUtil
import org.elixir_lang.psi.CallDefinitionClause.nameArityRange
import org.elixir_lang.psi.ElixirFile
import org.elixir_lang.psi.Modular
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.Named

import org.elixir_lang.psi.impl.ElixirPsiImplUtil.ENTRANCE
import org.elixir_lang.psi.impl.call.finalArguments
import org.elixir_lang.psi.impl.call.keywordArgument
import org.elixir_lang.psi.impl.maybeModularNameToModular
import org.elixir_lang.psi.impl.stripAccessExpression
import org.elixir_lang.psi.putInitialVisitedElement
import org.elixir_lang.psi.putVisitedElement
import org.elixir_lang.psi.scope.ResolveResultOrderedSet
import org.elixir_lang.psi.scope.maxScope
import org.elixir_lang.structure_view.element.CallDefinitionHead

class MultiResolve
private constructor(private val name: String,
                    private val resolvedFinalArity: Int,
                    private val incompleteCode: Boolean) : org.elixir_lang.psi.scope.CallDefinitionClause() {
    override fun executeOnCallDefinitionClause(element: Call, state: ResolveState): Boolean =
        nameArityRange(element)?.let { nameArityRange ->
            val name = nameArityRange.name

            if (name.startsWith(this.name)) {
                val arityInterval = ArityInterval.arityInterval(nameArityRange, state)
                val validResult = (resolvedFinalArity in arityInterval) && name == this.name

                addToResolveResults(element, validResult, state)
            } else {
                null
            }
        } ?: true

    override fun executeOnDelegation(element: Call, state: ResolveState): Boolean =
            element.finalArguments()?.takeIf { it.size == 2 }?.let { arguments ->
                val head = arguments[0]

                CallDefinitionHead.nameArityRange(head)?.let { headNameArityRange ->
                    val headName = headNameArityRange.name
                    val name = element.keywordArgument("as")?.stripAccessExpression()?.let { it as? Call }?.functionName() ?: headName

                    if (name.startsWith(this.name)) {
                        element.keywordArgument("to")?.let { definingModuleName ->
                            definingModuleName.maybeModularNameToModular(element.containingFile, useCall = null)?.let { modular ->
                                val delegationState = state.put(DEFDELEGATE_CALL, element).putVisitedElement(element)

                                Modular.callDefinitionClauseCallWhile(modular, delegationState) { callDefinitionClauseCall, state ->
                                    org.elixir_lang.psi.CallDefinitionClause.nameArityRange(callDefinitionClauseCall)?.let { callNameArityRange ->
                                        val callName = callNameArityRange.name

                                        if (callName.startsWith(headName)) {
                                            val validResult = (resolvedFinalArity in callNameArityRange.arityRange) && name == this.name

                                            addToResolveResults(element, validResult, state)

                                        } else {
                                            null
                                        }
                                    }

                                    keepProcessing()
                                }
                            }
                        }
                    } else {
                        null
                    }
                }
            }
                    ?: keepProcessing()

    override fun keepProcessing(): Boolean = resolveResultOrderedSet.keepProcessing(incompleteCode)
    fun resolveResults(): Array<ResolveResult> = resolveResultOrderedSet.toTypedArray()

    private val resolveResultOrderedSet = ResolveResultOrderedSet()

    private fun addToResolveResults(call: Call, validResult: Boolean, state: ResolveState): Boolean =
            (call as? Named)?.nameIdentifier?.let { nameIdentifier ->
                if (PsiTreeUtil.isAncestor(state.get(ENTRANCE), nameIdentifier, false)) {
                    resolveResultOrderedSet.add(call, name, validResult)

                    false
                } else {
                    resolveResultOrderedSet.add(call, name, validResult)

                    state.get<Call>(DEFDELEGATE_CALL)?.let{ defdelegateCall ->
                        resolveResultOrderedSet.add(defdelegateCall, defdelegateCall.text, validResult)
                    }

                    state.get<Call>(IMPORT_CALL)?.let { importCall ->
                        resolveResultOrderedSet.add(importCall, importCall.text, validResult)
                    }

                    null
                }
            } ?: true

    companion object {
        @JvmOverloads
        @JvmStatic
        fun resolveResults(name: String,
                           resolvedFinalArity: Int,
                           incompleteCode: Boolean,
                           entrance: PsiElement,
                           resolveState: ResolveState = ResolveState.initial()): Array<ResolveResult> {
            val multiResolve = MultiResolve(name, resolvedFinalArity, incompleteCode)
            val maxScope = maxScope(entrance)

            PsiTreeUtil.treeWalkUp(
                    multiResolve,
                    entrance,
                    maxScope,
                    resolveState.put(ENTRANCE, entrance).putInitialVisitedElement(entrance)
            )

            return multiResolve.resolveResults()
        }

    }
}
