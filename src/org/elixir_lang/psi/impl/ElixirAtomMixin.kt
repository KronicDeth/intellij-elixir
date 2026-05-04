package org.elixir_lang.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.HintedReferenceHost
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceService
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry

/**
 * Mixin base class for [org.elixir_lang.psi.impl.ElixirAtomImpl].
 *
 * Implements [HintedReferenceHost] so that `PsiReferenceService.getReferences()` queries
 * both intrinsic references (from `getReference()`) and contributed references
 * (from `PsiReferenceContributor` registrations, e.g. `MfaTupleReferenceContributor`).
 *
 * Without this, the platform's `PsiReferenceServiceImpl.doGetReferences()` only calls
 * `element.getReferences()` (which returns only intrinsic references) for elements that
 * do not implement `ContributedReferenceHost` or `HintedReferenceHost`.
 */
abstract class ElixirAtomMixin(node: ASTNode) : ASTWrapperPsiElement(node), HintedReferenceHost {
    override fun getReferences(hints: PsiReferenceService.Hints): Array<PsiReference> {
        val intrinsic = reference
        val contributed = ReferenceProvidersRegistry.getReferencesFromProviders(this, hints)

        return if (intrinsic != null) {
            arrayOf(intrinsic, *contributed)
        } else {
            contributed
        }
    }

    override fun getReferences(): Array<PsiReference> = getReferences(PsiReferenceService.Hints.NO_HINTS)

    override fun shouldAskParentForReferences(hints: PsiReferenceService.Hints): Boolean = true
}
