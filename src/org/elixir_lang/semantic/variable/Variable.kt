package org.elixir_lang.semantic.variable

import com.intellij.psi.PsiElement
import org.elixir_lang.psi.ElixirVariable
import org.elixir_lang.semantic.Variable

class Variable(val elixirVariable: ElixirVariable) : Variable {
    override val psiElement: PsiElement
        get() = elixirVariable
    override val name: String by lazy {
        TODO()
    }
}
