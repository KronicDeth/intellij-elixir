package org.elixir_lang.psi.scope

import com.intellij.psi.PsiElement
import org.elixir_lang.psi.ElixirFile

fun maxScope(entrance: PsiElement): PsiElement {
    val containingFile = entrance.containingFile

    return (containingFile as? ElixirFile)?.viewFile() ?: containingFile
}
