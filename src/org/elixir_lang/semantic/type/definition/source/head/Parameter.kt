package org.elixir_lang.semantic.type.definition.source.head

import com.intellij.psi.ElementDescriptionLocation
import com.intellij.psi.PsiElement
import org.elixir_lang.psi.UnqualifiedNoArgumentsCall

class Parameter(
    val head: org.elixir_lang.semantic.type.definition.source.Head,
    val unqualifiedNoArgumentsCall: UnqualifiedNoArgumentsCall<*>
) : org.elixir_lang.semantic.type.definition.head.Parameter {
    override val psiElement: PsiElement
        get() = unqualifiedNoArgumentsCall

    override fun elementDescription(location: ElementDescriptionLocation): String? {
        TODO("Not yet implemented")
    }
}
