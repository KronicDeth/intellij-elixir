package org.elixir_lang.find_usages

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiPolyVariantReference
import com.intellij.psi.PsiReference

internal fun PsiReference.toPsiElementList(): List<PsiElement> =
        if (this is PsiPolyVariantReference) {
            this.toPsiElementList()
        } else {
            resolve()?.let { listOf(it) } ?: emptyList()
        }

private fun PsiPolyVariantReference.toPsiElementList(): List<PsiElement> =
        multiResolve(true)
                .filter { it.isValidResult }
                .mapNotNull { it.element }
