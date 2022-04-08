package org.elixir_lang.semantic.unquote

import com.intellij.psi.ElementDescriptionLocation
import com.intellij.psi.PsiElement
import org.elixir_lang.semantic.Unquote

class Call(val call: org.elixir_lang.psi.call.Call) : Unquote {
    override val psiElement: PsiElement
        get() = call

    override fun elementDescription(location: ElementDescriptionLocation): String? {
        TODO("Not yet implemented")
    }
}
