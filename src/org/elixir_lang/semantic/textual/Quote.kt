package org.elixir_lang.semantic.textual

import com.intellij.psi.ElementDescriptionLocation
import com.intellij.psi.PsiElement
import org.elixir_lang.psi.Quote
import org.elixir_lang.semantic.Textual

class Quote(val quote: Quote) : Textual {
    override val psiElement: PsiElement
        get() = quote

    override fun elementDescription(location: ElementDescriptionLocation): String? {
        TODO("Not yet implemented")
    }
}
