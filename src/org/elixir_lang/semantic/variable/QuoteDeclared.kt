package org.elixir_lang.semantic.variable

import com.intellij.psi.PsiElement
import org.elixir_lang.semantic.Variable

class QuoteDeclared(override val psiElement: PsiElement) : Variable {
    override val name: String
        get() = TODO("Not yet implemented")
}
