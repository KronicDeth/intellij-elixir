package org.elixir_lang.semantic.implementation.call

import com.intellij.psi.ElementDescriptionLocation
import com.intellij.psi.PsiElement

class ForName(
    override val implementation: org.elixir_lang.semantic.implementation.Call,
    val forNameElement: PsiElement
) :
    org.elixir_lang.semantic.implementation.ForName {
    override val psiElement: PsiElement
        get() = forNameElement
    override val canonicalName: String?
        get() = TODO("Not yet implemented")
    override val canonicalNameSet: Set<String>
        get() = TODO("Not yet implemented")
    override val protocolName: String
        get() = TODO("Not yet implemented")
    override val forName: String
        get() = TODO("Not yet implemented")

    override fun elementDescription(location: ElementDescriptionLocation): String? {
        TODO("Not yet implemented")
    }
}
