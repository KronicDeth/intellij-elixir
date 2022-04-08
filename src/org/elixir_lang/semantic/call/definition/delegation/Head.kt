package org.elixir_lang.semantic.call.definition.delegation

import com.intellij.psi.ElementDescriptionLocation
import com.intellij.psi.PsiElement
import org.elixir_lang.NameArityInterval
import org.elixir_lang.psi.call.Call
import org.elixir_lang.semantic.Semantic
import org.elixir_lang.semantic.call.definition.Delegation
import org.elixir_lang.semantic.call.definition.delegation.head.Parameter

class Head(val delegation: Delegation, val call: Call) : Semantic {
    override val psiElement: PsiElement
        get() = call
    val nameArityInterval: NameArityInterval? by lazy {
        TODO()
    }
    val parameters: List<Parameter> by lazy {
        TODO()
    }

    override fun elementDescription(location: ElementDescriptionLocation): String? {
        TODO("Not yet implemented")
    }
}
