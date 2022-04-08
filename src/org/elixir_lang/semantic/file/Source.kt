package org.elixir_lang.semantic.file

import com.intellij.psi.ElementDescriptionLocation
import com.intellij.psi.PsiElement
import org.elixir_lang.psi.ElixirFile
import org.elixir_lang.semantic.File
import org.elixir_lang.semantic.Modular

class Source(val elixirFile: ElixirFile) : File {
    override val psiElement: PsiElement
        get() = elixirFile
    override val modulars: List<Modular>
        get() = TODO("Not yet implemented")

    override fun elementDescription(location: ElementDescriptionLocation): String? {
        TODO("Not yet implemented")
    }
}
