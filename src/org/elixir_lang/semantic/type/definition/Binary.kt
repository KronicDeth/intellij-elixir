package org.elixir_lang.semantic.type.definition

import com.intellij.psi.ElementDescriptionLocation
import com.intellij.psi.PsiElement
import org.elixir_lang.NameArity
import org.elixir_lang.beam.psi.TypeDefinition
import org.elixir_lang.semantic.type.Definition

class Binary(val typeDefinition: TypeDefinition) : Definition {
    override val psiElement: PsiElement
        get() = typeDefinition
    override val nameArity: NameArity = typeDefinition.nameArity
    override val head: Head
        get() = TODO("Not yet implemented")
    override val body: Body
        get() = TODO("Not yet implemented")
    override val guard: Guard?
        get() = TODO("Not yet implemented")

    override fun elementDescription(location: ElementDescriptionLocation): String? {
        TODO("Not yet implemented")
    }
}
