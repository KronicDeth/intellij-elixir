package org.elixir_lang.semantic.ambiguous

import com.intellij.psi.ElementDescriptionLocation
import com.intellij.psi.PsiElement
import org.elixir_lang.psi.UnqualifiedNoArgumentsCall
import org.elixir_lang.semantic.Semantic

class Call(val unqualifiedNoArgumentsCall: UnqualifiedNoArgumentsCall<*>) : Semantic {
    override val psiElement: PsiElement
        get() = unqualifiedNoArgumentsCall

    override fun elementDescription(location: ElementDescriptionLocation): String? {
        TODO("Not yet implemented")
    }
}
