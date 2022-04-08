package org.elixir_lang.semantic

import com.intellij.psi.ElementDescriptionLocation
import com.intellij.psi.PsiElement
import org.elixir_lang.psi.ElixirTuple

class Tuple(val elixirTuple: ElixirTuple) : Patternable {
    override val psiElement: PsiElement
        get() = elixirTuple

    override fun elementDescription(location: ElementDescriptionLocation): String? {
        TODO("Not yet implemented")
    }
}
