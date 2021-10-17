package org.elixir_lang.psi

import com.intellij.psi.PsiElement
import org.elixir_lang.psi.operation.Match

object Variable {
    @JvmStatic
    fun isDeclaration(element: PsiElement): Boolean =
            element is UnqualifiedNoArgumentsCall<*> && element.resolvedFinalArity() == 0 && element.parent is Match
}
