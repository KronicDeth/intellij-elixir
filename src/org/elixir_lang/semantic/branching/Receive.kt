package org.elixir_lang.semantic.branching

import com.intellij.psi.ElementDescriptionLocation
import com.intellij.psi.PsiElement
import org.elixir_lang.psi.call.Call
import org.elixir_lang.semantic.Branching
import org.elixir_lang.semantic.Semantic

class Receive(val call: Call) : Branching {
    override val psiElement: PsiElement
        get() = TODO("Not yet implemented")
    override val branches: List<Semantic>
        get() = TODO("Not yet implemented")

    override fun elementDescription(location: ElementDescriptionLocation): String? {
        TODO("Not yet implemented")
    }
}
