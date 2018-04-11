package org.elixir_lang

import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import org.elixir_lang.psi.ElixirIdentifier
import org.elixir_lang.psi.call.Call

class TargetElementEvaluator : com.intellij.codeInsight.TargetElementEvaluatorEx2() {
    override fun adjustTargetElement(editor: Editor?, offset: Int, flags: Int, targetElement: PsiElement): PsiElement? =
        when (targetElement) {
            is ElixirIdentifier -> adjustTargetElement(targetElement)
            else -> super.adjustTargetElement(editor, offset, flags, targetElement)
        }

    override fun getElementByReference(reference: PsiReference, flags: Int): PsiElement? = reference.resolve()

    private fun adjustTargetElement(targetElement: ElixirIdentifier): PsiElement? =
            targetElement.parent as? Call ?: targetElement
}
