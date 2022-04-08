package org.elixir_lang.semantic.branching

import com.intellij.psi.ElementDescriptionLocation
import org.elixir_lang.psi.call.Call
import org.elixir_lang.semantic.Branching
import org.elixir_lang.semantic.Semantic

class Cond(override val psiElement: Call) : Branching {
    override val branches: List<Semantic>
        get() = TODO("Not yet implemented")

    override fun elementDescription(location: ElementDescriptionLocation): String? {
        TODO("Not yet implemented")
    }
}
