package org.elixir_lang.psi

import com.intellij.psi.ElementDescriptionLocation
import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveState
import com.intellij.psi.util.isAncestor
import com.intellij.usageView.UsageViewTypeLocation
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.name.Function.USE
import org.elixir_lang.psi.call.name.Module.KERNEL
import org.elixir_lang.psi.impl.ElixirPsiImplUtil.ENTRANCE
import org.elixir_lang.psi.impl.call.finalArguments
import org.elixir_lang.psi.impl.maybeModularNameToModulars

/**
 * A `use` call
 */
object Use {
    /**
     * Calls `keepProcessing` on each element added to the scope from the `quote` block inside the `__using__`
     * macro called by `useCall` while `keepProcessing` returns `true`.  Stops the first time `keepProcessing`
     * returns `false`.
     */
    fun treeWalkUp(useCall: Call, resolveState: ResolveState, keepProcessing: (PsiElement, ResolveState) -> Boolean): Boolean {
        var accumulatedKeepProcessing = true

        // don't descend back into `use` when the entrance is the alias to the `use` like `MyAlias` in `use MyAlias`.
        if (!useCall.isAncestor(resolveState.get(ENTRANCE))) {
            outer@ for (modular in modulars(useCall)) {
                for (definer in Using.definers(modular)) {
                    val childResolveState = resolveState.putVisitedElement(definer)

                    accumulatedKeepProcessing = Using.treeWalkUp(
                            usingCall = definer,
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

        return accumulatedKeepProcessing
    }

    fun elementDescription(@Suppress("UNUSED_PARAMETER") call: Call, location: ElementDescriptionLocation): String? {
        var elementDescription: String? = null

        if (location === UsageViewTypeLocation.INSTANCE) {
            elementDescription = "use"
        }

        return elementDescription
    }

    /**
     * Whether `call` is a `use Module` or `use Module, opts` call.
     */
    @JvmStatic
    fun `is`(call: Call): Boolean =
        call.isCalling(KERNEL, USE, 1) || call.isCalling(KERNEL, USE, 2);

    /**
     * The modular that is used by `useCall`.
     *
     * @param useCall a [Call] where [is] is `true`.
     * @return `defmodule`, `defimpl`, or `defprotocol` used by `useCall`.  It can be `null` if Alias passed to
     *    `useCall` cannot be resolved.
     */
    fun modulars(useCall: Call): Set<Call> =
            useCall
                    .finalArguments()
                    ?.firstOrNull()
                    ?.maybeModularNameToModulars(maxScope = useCall.parent, useCall = useCall, incompleteCode = false)
                    ?: emptySet()
}
