package org.elixir_lang.injection

import com.intellij.psi.LiteralTextEscaper
import com.intellij.psi.PsiElement
import org.elixir_lang.injection.markdown.Injector
import org.elixir_lang.psi.AtUnqualifiedNoParenthesesCall
import org.elixir_lang.psi.ElixirNoParenthesesKeywords
import org.elixir_lang.psi.Parent
import org.elixir_lang.psi.Sigil

object PsiLanguageInjectionHost {
    @JvmStatic
    fun isValidHost(psiElement: PsiElement): Boolean {
        if (psiElement as? Sigil != null) {
            return true
        }

        return when (val greatGrandParent = psiElement.parent?.parent?.parent) {
            is AtUnqualifiedNoParenthesesCall<*> -> Injector.isValidHost(greatGrandParent)
            is ElixirNoParenthesesKeywords -> {
                greatGrandParent
                    .parent
                    ?.parent
                    ?.let { it as? AtUnqualifiedNoParenthesesCall<*> }
                    ?.let(Injector.Companion::isValidHost)
                    ?: false
            }
            else -> false
        }
    }

    @JvmStatic
    fun createLiteralTextEscaper(parent: Parent): LiteralTextEscaper<Parent> =
        org.elixir_lang.injection.markdown.LiteralTextEscaper(parent)
}
