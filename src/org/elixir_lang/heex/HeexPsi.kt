package org.elixir_lang.heex

import com.intellij.psi.PsiElement

/**
 * Whether [this] belongs to a HEEx view provider - a `.heex` file or a `~H` sigil's injected
 * fragment - regardless of which of its roots (HEEx, HTML, Elixir) the element sits in.
 */
fun PsiElement.isInHeex(): Boolean =
    containingFile?.viewProvider?.hasLanguage(HeexLanguage.INSTANCE) == true
