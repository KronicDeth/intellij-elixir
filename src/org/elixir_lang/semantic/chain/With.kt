package org.elixir_lang.semantic.chain

import com.intellij.psi.ElementDescriptionLocation
import com.intellij.psi.PsiElement
import org.elixir_lang.psi.call.Call
import org.elixir_lang.semantic.Branching
import org.elixir_lang.semantic.Chain
import org.elixir_lang.semantic.Semantic

class With(val call: Call) : Chain, Branching {
    override val psiElement: PsiElement
        get() = call
    override val clauses: List<Clause>
        get() = TODO("Not yet implemented")
    override val branches: List<Semantic>
        get() = TODO("Not yet implemented")

    override fun elementDescription(location: ElementDescriptionLocation): String? {
        TODO("Not yet implemented")
    }
}
