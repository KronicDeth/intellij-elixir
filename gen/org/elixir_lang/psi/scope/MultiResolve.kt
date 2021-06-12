package org.elixir_lang.psi.scope

import com.intellij.openapi.util.Condition
import com.intellij.psi.ResolveResult
import com.intellij.util.containers.ContainerUtil
import java.util.*

object MultiResolve {
    /*
     * Constants
     */

    val HAS_VALID_RESULT_CONDITION: Condition<ResolveResult> = Condition { resolveResult -> resolveResult.isValidResult }

    /*
     * Public Static Methods
     */

    fun addToResolveResultList(resolveResultList: MutableList<ResolveResult>?,
                               resolveResult: ResolveResult): List<ResolveResult> {
        val nonNullResolveResultList = resolveResultList ?: mutableListOf()
        nonNullResolveResultList.add(resolveResult)

        return nonNullResolveResultList
    }

    /**
     * Whether the `resolveResults` has any [ResolveResult] where [ResolveResult.isValidResult]
     * is `true`.
     *
     * @return `false` if `resolveResults` is `null`; otherwise, `true` if the
     * `resolveResults` has any [ResolveResult] where [ResolveResult.isValidResult] is
     * `true`.
     */
    fun hasValidResult(resolveResultList: List<ResolveResult>?): Boolean {
        var hasValidResult = false

        if (resolveResultList != null) {
            hasValidResult = ContainerUtil.exists(resolveResultList, HAS_VALID_RESULT_CONDITION)
        }

        return hasValidResult
    }

    /**
     * Keep trying to resolve the reference if `resolveResults` does not have a valid result or the code is
     * incomplete.
     *
     * @return `false` if [.hasValidResult] or `incompleteCode` is `false`, so only one
     * valid result is allowed.
     */
    fun keepProcessing(incompleteCode: Boolean, resolveResultList: List<ResolveResult>?): Boolean =
        incompleteCode || !hasValidResult(resolveResultList)
}
