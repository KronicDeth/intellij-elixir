package org.elixir_lang.psi

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiPolyVariantReference
import com.intellij.psi.ResolveState
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.name.Function.*
import org.elixir_lang.psi.call.name.Module.KERNEL
import org.elixir_lang.psi.impl.call.finalArguments
import org.elixir_lang.psi.impl.call.stabBodyChildExpressions
import org.elixir_lang.psi.impl.maybeModularNameToModulars
import org.elixir_lang.psi.impl.stripAccessExpression
import org.elixir_lang.psi.scope.WhileIn.whileIn
import org.elixir_lang.semantic.Quote
import org.elixir_lang.semantic.call.definition.Clause
import org.elixir_lang.semantic.call.definition.clause.Time
import org.elixir_lang.semantic.semantic

object Using {
    fun treeWalkUp(
        usingCall: Call,
        useCall: Call?,
        resolveState: ResolveState,
        keepProcessing: (PsiElement, ResolveState) -> Boolean
    ): Boolean =
        usingCall
            .stabBodyChildExpressions(forward = false)
            ?.filterIsInstance<Call>()
            // Because `forward = false`, `firstOrNull` gets the last Call in the `do` block
            ?.firstOrNull()
            ?.takeUnlessHasBeenVisited(resolveState)
            ?.let { lastChildCall -> treeWalkUpFromLastChildCall(lastChildCall, useCall, resolveState, keepProcessing) }
            ?: true

    private fun treeWalkUpFromLastChildCall(
        lastChildCall: Call,
        useCall: Call?,
        resolveState: ResolveState,
        keepProcessing: (PsiElement, ResolveState) -> Boolean
    ): Boolean {
        val resolvedModuleName = lastChildCall.resolvedModuleName()
        val functionName = lastChildCall.functionName()

        return if (resolvedModuleName != null && functionName != null) {
            when {
                resolvedModuleName == KERNEL && functionName == QUOTE -> {
                    Quote.treeWalkUp(lastChildCall, resolveState, keepProcessing)
                }

                resolvedModuleName == KERNEL && functionName == APPLY -> {
                    lastChildCall.finalArguments()?.let { arguments ->
                        // TODO pipelines to apply/3
                        if (arguments.size == 3) {
                            arguments[0].let { maybeModularName ->
                                val modulars = maybeModularName.maybeModularNameToModulars(
                                    maxScope = maybeModularName.containingFile,
                                    useCall = useCall,
                                    incompleteCode = false
                                )

                                var accumlatedKeepProcessing = true

                                for (modular in modulars) {
                                    val modularResolveState = resolveState.putVisitedElement(modular.psiElement)

                                    val name = useCall?.finalArguments()?.let { arguments ->
                                        if (arguments.size == 2) {
                                            when (val which = arguments[1].stripAccessExpression()) {
                                                is ElixirAtom -> if (which.line == null) {
                                                    which.lastChild.text
                                                } else {
                                                    null
                                                }
                                                else -> null
                                            }
                                        } else {
                                            null
                                        }
                                    }

                                    val callDefinitions = modular.exportedCallDefinitions

                                    val filteredCallDefinitions = if (name != null) {
                                        callDefinitions
                                            .filter { it.nameArityInterval?.name == name }
                                    } else {
                                        callDefinitions
                                            .filter { it.time == Time.RUN }
                                    }

                                    val walkable = filteredCallDefinitions
                                        .flatMap { it.clauses }
                                        .filter { !it.compiled }
                                        .mapNotNull { it.psiElement as? Call }

                                    accumlatedKeepProcessing = whileIn(walkable) {
                                        treeWalkUp(it, useCall, modularResolveState, keepProcessing)
                                    }

                                    if (!accumlatedKeepProcessing) {
                                        break
                                    }
                                }

                                accumlatedKeepProcessing
                            }
                        } else {
                            true
                        }
                    } ?: true
                }
                resolvedModuleName == KERNEL && functionName == CASE -> {
                    val lastChildCallResolveState = resolveState.putVisitedElement(lastChildCall)

                    Case.treeWalkUp(lastChildCall, lastChildCallResolveState, keepProcessing)
                }
                else -> {
                    var accumulatedKeepProcessing = true

                    for (reference in lastChildCall.references) {
                        val resolvedList: List<PsiElement> = if (reference is PsiPolyVariantReference) {
                            reference
                                .multiResolve(false)
                                .mapNotNull { it.element }
                        } else {
                            reference.resolve()?.let { listOf(it) } ?: emptyList()
                        }.filter { !resolveState.hasBeenVisited(it) }

                        for (resolved in resolvedList) {
                            accumulatedKeepProcessing = if (resolved is Call && resolved.semantic is Clause) {
                                val resolvedResolveSet = resolveState.putVisitedElement(resolved)

                                treeWalkUp(
                                    usingCall = resolved,
                                    useCall = useCall,
                                    resolveState = resolvedResolveSet,
                                    keepProcessing = keepProcessing
                                )
                            } else {
                                true
                            }

                            if (!accumulatedKeepProcessing) {
                                break
                            }
                        }

                        if (!accumulatedKeepProcessing) {
                            break
                        }
                    }

                    accumulatedKeepProcessing
                }
            }
        } else {
            true
        }
    }
}
