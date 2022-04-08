package org.elixir_lang.semantic.branching.conditional

import com.intellij.psi.ElementDescriptionLocation
import org.elixir_lang.psi.call.Call
import org.elixir_lang.semantic.Semantic
import org.elixir_lang.semantic.branching.Conditional

class Unless(override val psiElement: Call) : Conditional {
    override val condition: Semantic
        get() = TODO("Not yet implemented")
    override val branches: List<Semantic>
        get() = TODO("Not yet implemented")

    override fun elementDescription(location: ElementDescriptionLocation): String? {
        TODO("Not yet implemented")
    }
}
