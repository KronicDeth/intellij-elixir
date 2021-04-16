package org.elixir_lang.psi.scope.call_definition_clause

import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveResult
import com.intellij.psi.ResolveState
import com.intellij.psi.util.PsiTreeUtil
import org.elixir_lang.psi.CallDefinitionClause.nameArityRange
import org.elixir_lang.psi.ElixirAtom
import org.elixir_lang.psi.Modular
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.Named

import org.elixir_lang.psi.impl.ElixirPsiImplUtil.ENTRANCE
import org.elixir_lang.psi.impl.call.finalArguments
import org.elixir_lang.psi.impl.call.keywordArgument
import org.elixir_lang.psi.impl.maybeModularNameToModulars
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

    override fun executeOnDelegation(element: Call, state: ResolveState): Boolean {
        element.finalArguments()?.takeIf { it.size == 2 }?.let { arguments ->
            val head = arguments[0]

            CallDefinitionHead.nameArityRange(head)?.let { headNameArityRange ->
                val headName = headNameArityRange.name

                if (headName.startsWith(this.name)) {
                    val headValidResult = (resolvedFinalArity in headNameArityRange.arityRange) && (headName == this.name)

                    // the defdelegate is valid or invalid regardless of whether the `to:` (and `:as` resolves as
                    // `defdelegate` still defines a function in the module with the head's name and arity even if it
                    // will fail at runtime to call the delegated function
                    addToResolveResults(element, headValidResult, state)

                    element.keywordArgument("to")?.let { definingModuleName ->
                        val modulars = definingModuleName.maybeModularNameToModulars(element.containingFile, useCall = null, incompleteCode = incompleteCode)

                        if (modulars.isNotEmpty()) {
                            val nameInDefiningModule = element.keywordArgument("as")?.let { it as? ElixirAtom }?.node?.lastChildNode?.text
                                    ?: headName

                            for (modular in modulars) {
                                // Call recursively to get all the proper `for` and `use` handling.
                                val modularResolveResults = resolveResults(nameInDefiningModule, resolvedFinalArity, incompleteCode, modular)

                                for (modularResultResult in modularResolveResults) {
                                    modularResultResult.element?.let { it as Call }?.let { call ->
                                        addToResolveResults(call, modularResultResult.isValidResult, state)
                                    }
                                }

                                if (!keepProcessing()) {
                                    break
                                }
                            }
                        }
                    }
                }
            }
        }

        return keepProcessing()
    }

    override fun keepProcessing(): Boolean = resolveResultOrderedSet.keepProcessing(incompleteCode)
    fun resolveResults(): List<ResolveResult> = resolveResultOrderedSet.toList()

    private val resolveResultOrderedSet = ResolveResultOrderedSet()

    private fun addToResolveResults(call: Call, validResult: Boolean, state: ResolveState): Boolean =
            (call as? Named)?.nameIdentifier?.let { nameIdentifier ->
                if (PsiTreeUtil.isAncestor(state.get(ENTRANCE), nameIdentifier, false)) {
                    resolveResultOrderedSet.add(call, name, validResult)
                } else {
                    resolveResultOrderedSet.add(call, name, validResult)

                    state.get<Call>(IMPORT_CALL)?.let { importCall ->
                        resolveResultOrderedSet.add(importCall, importCall.text, validResult)
                    }
                }

                keepProcessing()
            } ?: true

    companion object {
        @JvmOverloads
        @JvmStatic
        fun resolveResults(name: String,
                           resolvedFinalArity: Int,
                           incompleteCode: Boolean,
                           entrance: PsiElement,
                           resolveState: ResolveState = ResolveState.initial()): List<ResolveResult> {
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
