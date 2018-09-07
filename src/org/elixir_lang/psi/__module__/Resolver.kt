package org.elixir_lang.psi.__module__

import com.intellij.psi.ResolveResult
import com.intellij.psi.ResolveState
import com.intellij.psi.impl.source.resolve.ResolveCache
import com.intellij.psi.util.PsiTreeUtil
import org.elixir_lang.psi.call.Call

object Resolver : ResolveCache.PolyVariantResolver<Reference> {
    override fun resolve(reference: Reference, incompleteCode: Boolean): Array<ResolveResult> =
        resolve(reference.element, reference.useCall)

    private fun resolve(call: Call, useCall: Call?): Array<ResolveResult> {
        val processor = PsiScopeProcessor(call, useCall)

        PsiTreeUtil.treeWalkUp(
                processor,
                call,
                call.containingFile,
                ResolveState.initial()
        )

        return processor.resolveResults
    }
}
