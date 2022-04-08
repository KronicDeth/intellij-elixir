package org.elixir_lang.semantic.call.usage

import com.intellij.psi.ElementDescriptionLocation
import com.intellij.psi.PsiElement
import org.elixir_lang.psi.call.Call
import org.elixir_lang.semantic.call.definition.clause.Using

class Call(val call: Call) : org.elixir_lang.semantic.call.Usage {
    override val psiElement: PsiElement
        get() = call
    override val using: Using
        get() = TODO("Not yet implemented")

    override fun elementDescription(location: ElementDescriptionLocation): String? {
        TODO("Not yet implemented")
    }
}
