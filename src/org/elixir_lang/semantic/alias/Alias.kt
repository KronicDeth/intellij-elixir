package org.elixir_lang.semantic.alias

import com.intellij.psi.ElementDescriptionLocation
import com.intellij.psi.PsiElement
import org.elixir_lang.psi.ElixirAlias
import org.elixir_lang.semantic.Alias

class Alias(val elixirAlias: ElixirAlias) : Alias {
    override val psiElement: PsiElement
        get() = elixirAlias
    override val name: String = elixirAlias.name
    override val suffix: Alias = this
    override val nested: Set<Alias>
        get() = TODO("Not yet implemented")

    override fun elementDescription(location: ElementDescriptionLocation): String? {
        TODO("Not yet implemented")
    }
}
