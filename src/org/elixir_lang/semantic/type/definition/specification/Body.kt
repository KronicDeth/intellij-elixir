package org.elixir_lang.semantic.type.definition.specification

import com.intellij.psi.ElementDescriptionLocation
import com.intellij.psi.PsiElement
import org.elixir_lang.psi.call.Call
import org.elixir_lang.semantic.type.definition.source.Specification

class Body(val specification: Specification, val call: Call) : org.elixir_lang.semantic.type.definition.Body {
    override val psiElement: PsiElement
        get() = call

    override fun elementDescription(location: ElementDescriptionLocation): String? {
        TODO("Not yet implemented")
    }
}
