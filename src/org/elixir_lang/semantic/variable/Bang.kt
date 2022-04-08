package org.elixir_lang.semantic.variable

import com.intellij.psi.PsiElement
import org.elixir_lang.psi.call.Call

class Bang(val call: Call) : org.elixir_lang.semantic.Variable {
    override val psiElement: PsiElement
        get() = TODO("Not yet implemented")
    override val name: String
        get() = TODO("Not yet implemented")
}
