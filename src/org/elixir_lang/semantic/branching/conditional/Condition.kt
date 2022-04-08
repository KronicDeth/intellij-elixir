package org.elixir_lang.semantic.branching.conditional

import com.intellij.psi.ElementDescriptionLocation
import com.intellij.psi.PsiElement
import org.elixir_lang.semantic.Semantic

class Condition(override val psiElement: PsiElement) : Semantic {
    override fun elementDescription(location: ElementDescriptionLocation): String? {
        TODO("Not yet implemented")
    }
}
