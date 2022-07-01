package org.elixir_lang.injection

import com.intellij.psi.LiteralTextEscaper
import com.intellij.psi.PsiElement
import org.elixir_lang.injection.markdown.Injector
import org.elixir_lang.psi.AtUnqualifiedNoParenthesesCall
import org.elixir_lang.psi.Parent

object PsiLanguageInjectionHost {
    @JvmStatic
    fun isValidHost(psiElement: PsiElement): Boolean =
        psiElement
            ?.parent
            ?.parent
            ?.parent
            ?.let { it as? AtUnqualifiedNoParenthesesCall<*> }
            ?.let(Injector.Companion::isValidHost)
            ?: false

    @JvmStatic
    fun createLiteralTextEscaper(parent: Parent): LiteralTextEscaper<Parent> =
        org.elixir_lang.injection.markdown.LiteralTextEscaper(parent)
}
