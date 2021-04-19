package org.elixir_lang

import com.intellij.psi.PsiPolyVariantReference
import com.intellij.psi.ResolveResult
import org.elixir_lang.errorreport.Logger

fun safeMultiResolve(reference: PsiPolyVariantReference, incompleteCode: Boolean): Array<ResolveResult> =
    try {
        reference.multiResolve(incompleteCode)
    } catch (stackOverflowError: StackOverflowError) {
        Logger.error(PsiPolyVariantReference::class.java, "StackOverflow resolving reference", reference.element)

        emptyArray()
    }
