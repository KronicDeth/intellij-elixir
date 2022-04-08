package org.elixir_lang.semantic.call.definition.clause.head

import com.intellij.psi.ElementDescriptionLocation
import com.intellij.psi.PsiElement
import org.elixir_lang.semantic.call.definition.clause.Head

class Parameter(
    override val head: Head,
    override val psiElement: PsiElement
) :
    org.elixir_lang.semantic.call.definition.Parameter {
    override fun elementDescription(location: ElementDescriptionLocation): String? {
        TODO("Not yet implemented")
    }
}
