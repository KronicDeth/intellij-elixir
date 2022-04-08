package org.elixir_lang.semantic.chain

import com.intellij.psi.ElementDescriptionLocation
import com.intellij.psi.PsiElement
import org.elixir_lang.psi.call.Call
import org.elixir_lang.semantic.Chain
import org.elixir_lang.semantic.Semantic

class For(val call: Call) : Chain {
    override val psiElement: PsiElement
        get() = call
    override val clauses: List<Clause>
        get() = TODO("Not yet implemented")
    val body: Semantic
        get() = TODO()

    override fun elementDescription(location: ElementDescriptionLocation): String? {
        TODO("Not yet implemented")
    }
}
