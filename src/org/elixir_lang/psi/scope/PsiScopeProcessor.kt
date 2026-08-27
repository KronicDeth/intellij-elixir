package org.elixir_lang.psi.scope

import com.intellij.injected.editor.VirtualFileWindow
import com.intellij.psi.PsiElement
import org.elixir_lang.psi.ElixirFile

/**
 * Where [com.intellij.psi.util.PsiTreeUtil.treeWalkUp] stops (inclusive): the sibling view
 * module's file for a template with one ([ElixirFile.viewFile]), else the containing file. An
 * injected fragment (a `~H` sigil's Elixir root) has no view file, so it takes its injection
 * host's boundary instead; otherwise the walk would stop at the fragment before
 * [ElixirFile.getContext] could climb into the host module.
 */
fun maxScope(entrance: PsiElement): PsiElement {
    val containingFile = entrance.containingFile

    return if (containingFile is ElixirFile && containingFile.virtualFile is VirtualFileWindow) {
        containingFile.context?.let { maxScope(it) } ?: containingFile
    } else {
        (containingFile as? ElixirFile)?.viewFile() ?: containingFile
    }
}
