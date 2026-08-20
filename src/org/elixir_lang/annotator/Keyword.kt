package org.elixir_lang.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.openapi.project.DumbAware
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.tree.TokenSet
import com.intellij.psi.util.PsiTreeUtil
import org.elixir_lang.ElixirSyntaxHighlighter
import org.elixir_lang.psi.AtUnqualifiedNoParenthesesCall
import org.elixir_lang.psi.ElixirTypes

/**
 * Applies keyword highlighting to `do`, `end`, `fn`, `after`, `catch`, `rescue`, and `else`
 * tokens in injected Elixir fragments inside documentation heredocs.
 *
 * When Markdown and Elixir are both injected into the same host heredoc element,
 * the platform's lexer-based syntax highlighting for the Elixir injection may not
 * be applied.  Annotator-level highlighting (which uses `enforcedTextAttributes`)
 * is reliably applied regardless of multi-injection interactions.
 *
 * Tokens like `def`, `Logger`, and `Enum` already receive annotator-level highlighting
 * from [Callable] and [Alias], so they display correctly.  The structural keywords
 * (`do`, `end`, etc.) only had lexer-level highlighting, making them appear unhighlighted.
 */
class Keyword : Annotator, DumbAware {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (element !is LeafPsiElement) return
        if (!KEYWORD_TOKEN_SET.contains(element.elementType)) return

        val containingFile = element.containingFile ?: return
        val project = element.project
        val injectedLanguageManager = InjectedLanguageManager.getInstance(project)

        // Only apply in injected Elixir fragments, not in normal Elixir source files
        if (!injectedLanguageManager.isInjectedFragment(containingFile)) return

        // Verify the injection host is a documentation heredoc
        val injectionHost = injectedLanguageManager.getInjectionHost(containingFile) ?: return
        val atCall = PsiTreeUtil.getParentOfType(injectionHost, AtUnqualifiedNoParenthesesCall::class.java)
            ?: return

        if (!org.elixir_lang.injection.PsiLanguageInjectionHost.isDocumentationHost(atCall)) return

        Highlighter.highlight(holder, element as PsiElement, ElixirSyntaxHighlighter.KEYWORD)
    }

}

private val KEYWORD_TOKEN_SET = TokenSet.create(
    ElixirTypes.AFTER,
    ElixirTypes.CATCH,
    ElixirTypes.DO,
    ElixirTypes.ELSE,
    ElixirTypes.END,
    ElixirTypes.FN,
    ElixirTypes.RESCUE
)
