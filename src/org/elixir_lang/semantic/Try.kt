package org.elixir_lang.semantic

import com.intellij.psi.ElementDescriptionLocation
import com.intellij.psi.PsiElement
import org.elixir_lang.psi.call.Call

class Try(val call: Call) : Semantic {
    override val psiElement: PsiElement
        get() = call
    val body: Semantic
        get() = TODO()

    override fun elementDescription(location: ElementDescriptionLocation): String? {
        TODO("Not yet implemented")
    }
}
