package org.elixir_lang.semantic

import com.intellij.psi.ElementDescriptionLocation
import com.intellij.psi.PsiElement
import org.elixir_lang.psi.ElixirList

class List(val elixirList: ElixirList) : Patternable, Semantic {
    override val psiElement: PsiElement
        get() = elixirList

    override fun elementDescription(location: ElementDescriptionLocation): String? {
        TODO("Not yet implemented")
    }
}
