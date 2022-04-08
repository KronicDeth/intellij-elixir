package org.elixir_lang.semantic

import com.intellij.psi.ElementDescriptionLocation
import com.intellij.psi.PsiElement

class MatchQuestionMark(val call: org.elixir_lang.psi.call.Call) : Semantic {
    override val psiElement: PsiElement
        get() = call
    val pattern: Semantic by lazy {
        TODO()
    }

    override fun elementDescription(location: ElementDescriptionLocation): String? {
        TODO("Not yet implemented")
    }
}
