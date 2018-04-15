package org.elixir_lang.find_usages

import com.intellij.find.findUsages.FindUsagesHandler
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiPolyVariantReference
import com.intellij.psi.PsiReference

class Handler(psiElement: PsiElement) : FindUsagesHandler(psiElement) {
    override fun getPrimaryElements(): Array<PsiElement> =
        super
                .getPrimaryElements()
                .flatMap { it.references.toList() }
                .flatMap { psiReferenceToPsiElementList(it) }
                .toTypedArray()

    private fun psiReferenceToPsiElementList(psiReference: PsiReference): List<PsiElement> =
        if (psiReference is PsiPolyVariantReference) {
            psiReference
                    .multiResolve(true)
                    .filter { it.isValidResult }
                    .mapNotNull { it.element }
        } else {
            psiReference.resolve()?.let { listOf(it) } ?: emptyList()
        }
}
