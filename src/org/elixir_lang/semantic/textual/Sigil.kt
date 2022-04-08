package org.elixir_lang.semantic.textual

import com.intellij.psi.ElementDescriptionLocation
import com.intellij.psi.PsiElement
import org.elixir_lang.psi.Sigil
import org.elixir_lang.semantic.Textual

class Sigil(val sigil: Sigil) : Textual {
    override val psiElement: PsiElement
        get() = sigil

    override fun elementDescription(location: ElementDescriptionLocation): String? {
        TODO("Not yet implemented")
    }
}
