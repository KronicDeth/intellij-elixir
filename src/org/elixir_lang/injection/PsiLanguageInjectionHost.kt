package org.elixir_lang.injection

import com.intellij.psi.LiteralTextEscaper
import com.intellij.psi.PsiElement
import org.elixir_lang.psi.AtUnqualifiedNoParenthesesCall
import org.elixir_lang.psi.ElixirNoParenthesesKeywords
import org.elixir_lang.psi.Parent
import org.elixir_lang.psi.Sigil
import org.elixir_lang.reference.ModuleAttribute.Companion.DOCUMENTATION_NAME_SET
import org.elixir_lang.settings.ElixirExperimentalSettings

object PsiLanguageInjectionHost {
    @JvmStatic
    fun isValidHost(psiElement: PsiElement): Boolean {
        // If the element is a Sigil, then it is definitely a valid host
        // @todo make this more precise, to ~H etc
        if (ElixirExperimentalSettings.instance.state.enableHtmlInjection && psiElement as? Sigil != null) {
            return true
        }

        return when (val greatGrandParent = psiElement.parent?.parent?.parent) {
            is AtUnqualifiedNoParenthesesCall<*> -> isDocumentationHost(greatGrandParent)
            is ElixirNoParenthesesKeywords -> {
                greatGrandParent
                    .parent
                    ?.parent
                    ?.let { it as? AtUnqualifiedNoParenthesesCall<*> }
                    ?.let(::isDocumentationHost)
                    ?: false
            }
            else -> false
        }
    }

    /**
     * Returns `true` when [atUnqualifiedNoParenthesesCall] is an `@doc`, `@moduledoc`, `@typedoc`,
     * or other documentation attribute whose value may contain markdown.
     *
     * This is the single source of truth for the documentation-host check, used by both the
     * [PsiLanguageInjectionHost] `isValidHost` path and the markdown [org.elixir_lang.injection.markdown.Injector].
     */
    internal fun isDocumentationHost(atUnqualifiedNoParenthesesCall: AtUnqualifiedNoParenthesesCall<*>): Boolean =
        atUnqualifiedNoParenthesesCall.atIdentifier.lastChild?.text in DOCUMENTATION_NAME_SET

    @JvmStatic
    fun createLiteralTextEscaper(parent: Parent): LiteralTextEscaper<Parent> =
        org.elixir_lang.injection.markdown.LiteralTextEscaper(parent)
}
