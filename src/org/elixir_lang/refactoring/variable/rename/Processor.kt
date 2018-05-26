package org.elixir_lang.refactoring.variable.rename

import com.intellij.psi.PsiElement
import com.intellij.refactoring.rename.RenamePsiElementProcessor
import org.elixir_lang.refactoring.variable.rename.Handler.Companion.isAvailableOnResolved

class Processor : RenamePsiElementProcessor() {
    override fun canProcessElement(element: PsiElement): Boolean = isAvailableOnResolved(element)
}
