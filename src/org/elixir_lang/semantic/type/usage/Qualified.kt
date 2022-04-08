package org.elixir_lang.semantic.type.usage

import com.intellij.psi.ElementDescriptionLocation
import com.intellij.psi.PsiElement
import org.elixir_lang.psi.QualifiedNoArgumentsCall
import org.elixir_lang.semantic.type.Usage

class Qualified(val qualifiedNoArgumentsCall: QualifiedNoArgumentsCall<*>) : Usage {
    override val psiElement: PsiElement
        get() = qualifiedNoArgumentsCall

    override fun elementDescription(location: ElementDescriptionLocation): String? {
        TODO("Not yet implemented")
    }
}
