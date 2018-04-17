package org.elixir_lang

import com.intellij.codeInsight.TargetElementEvaluatorEx2
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import org.elixir_lang.psi.call.Call
import org.elixir_lang.reference.Callable

class TargetElementEvaluator : TargetElementEvaluatorEx2() {
    override fun getElementByReference(reference: PsiReference, flags: Int): PsiElement? =
        reference.resolve().let { resolved ->
            /* DO NOT let references to definers resolve as targets as it causes the definer to be the target, which
               then has a reference to the definer's macro */
            if (resolved != null && resolved is Call && Callable.isDefiner(resolved)) {
                reference.element
            } else {
                resolved
            }
        }
}
