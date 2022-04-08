package org.elixir_lang.psi

import com.intellij.psi.PsiElement

interface ModuleAttribute : PsiElement {
    companion object {
        @JvmStatic
        fun isDeclaration(element: PsiElement): Boolean = element is AtUnqualifiedNoParenthesesCall<*> && element.resolvedFinalArity() == 1
    }
}
