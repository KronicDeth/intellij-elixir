package org.elixir_lang.psi.__module__

import com.intellij.psi.ResolveResult
import com.intellij.psi.ResolveState
import com.intellij.psi.impl.source.resolve.ResolveCache
import com.intellij.psi.util.PsiTreeUtil
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.impl.ElixirPsiImplUtil.ENTRANCE
import org.elixir_lang.psi.putInitialVisitedElement

object Resolver : ResolveCache.PolyVariantResolver<Reference> {
    override fun resolve(reference: Reference, incompleteCode: Boolean): Array<ResolveResult> =
        resolve(reference.element, reference.useCall)

    private fun resolve(call: Call, useCall: Call?): Array<ResolveResult> {
        val processor = PsiScopeProcessor(call, useCall)

        PsiTreeUtil.treeWalkUp(
                processor,
                call,
                call.containingFile,
                ResolveState.initial().put(ENTRANCE, call).putInitialVisitedElement(call)
        )

        return processor.resolveResults
    }
}
