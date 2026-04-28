package org.elixir_lang.injection.markdown

import com.intellij.codeInsight.highlighting.HighlightErrorFilter
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.psi.PsiErrorElement
import com.intellij.psi.util.PsiTreeUtil
import org.elixir_lang.ElixirLanguage
import org.elixir_lang.injection.PsiLanguageInjectionHost
import org.elixir_lang.psi.AtUnqualifiedNoParenthesesCall

/**
 * Suppresses [PsiErrorElement] (parser syntax error) highlighting inside Elixir code fragments
 * injected into `@doc`/`@moduledoc`/`@typedoc` heredocs.
 *
 * Documentation code blocks are inherently partial — `iex>` lines are single expressions,
 * indented blocks are often snippets without full module context. The Elixir parser naturally
 * produces [PsiErrorElement] nodes for these fragments, but surfacing them as red squiggles
 * in the editor provides no value and creates noise.
 *
 * This filter preserves full lexer + annotator syntax highlighting (atoms, aliases, function
 * calls, operators, numbers, etc.) while hiding only the parser error markers.
 */
class DocCodeBlockHighlightErrorFilter : HighlightErrorFilter() {
    override fun shouldHighlightErrorElement(element: PsiErrorElement): Boolean {
        // Only suppress errors in Elixir language fragments
        if (element.language != ElixirLanguage) return true

        // Check if this element lives in an injected fragment
        val containingFile = element.containingFile ?: return true
        val project = element.project
        val injectedLanguageManager = InjectedLanguageManager.getInstance(project)

        if (!injectedLanguageManager.isInjectedFragment(containingFile)) return true

        // It's an injected Elixir fragment — find the injection host (a Heredoc element)
        // and walk up to the enclosing @doc/@moduledoc/@typedoc call.
        val injectionHost = injectedLanguageManager.getInjectionHost(containingFile) ?: return true
        val atCall = PsiTreeUtil.getParentOfType(injectionHost, AtUnqualifiedNoParenthesesCall::class.java)
            ?: return true

        // Suppress errors only if this is a documentation attribute
        return !PsiLanguageInjectionHost.isDocumentationHost(atCall)
    }
}
