package org.elixir_lang.psi

import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveState
import com.intellij.psi.util.isAncestor
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.impl.ElixirPsiImplUtil.ENTRANCE
import org.elixir_lang.psi.impl.call.finalArguments
import org.elixir_lang.psi.impl.maybeModularNameToModulars
import org.elixir_lang.semantic.Modular

/**
 * A `use` call
 */
object Use {
    /**
     * Calls `keepProcessing` on each element added to the scope from the `quote` block inside the `__using__`
     * macro called by `useCall` while `keepProcessing` returns `true`.  Stops the first time `keepProcessing`
     * returns `false`.
     */
    fun treeWalkUp(
        useCall: Call,
        resolveState: ResolveState,
        keepProcessing: (PsiElement, ResolveState) -> Boolean
    ): Boolean {
        var accumulatedKeepProcessing = true

        // don't descend back into `use` when the entrance is the alias to the `use` like `MyAlias` in `use MyAlias`.
        if (!useCall.isAncestor(resolveState.get(ENTRANCE))) {
            val useCallResolveState = resolveState.putVisitedElement(useCall)

            outer@ for (modular in modulars(useCall)) {
                for (definer in modular.usings) {
                    if (!definer.compiled) {
                        val definerCall = definer.psiElement.let { it as? Call }

                        if (definerCall != null) {
                            val childResolveState = useCallResolveState.putVisitedElement(definerCall)

                            accumulatedKeepProcessing = Using.treeWalkUp(
                                usingCall = definerCall,
                                useCall = useCall,
                                resolveState = childResolveState,
                                keepProcessing = keepProcessing
                            )

                            if (!accumulatedKeepProcessing) {
                                break@outer
                            }
                        }
                    }
                }
            }
        }

        return accumulatedKeepProcessing
    }

    /**
     * The modular that is used by `useCall`.
     *
     * @param useCall a [Call] where [is] is `true`.
     * @return `defmodule`, `defimpl`, or `defprotocol` used by `useCall`.  It can be `null` if Alias passed to
     *    `useCall` cannot be resolved.
     */
    fun modulars(useCall: Call): Set<Modular> =
        useCall
            .finalArguments()
            ?.firstOrNull()
            ?.maybeModularNameToModulars(maxScope = useCall.parent, useCall = useCall, incompleteCode = false)
            ?: emptySet()
}
