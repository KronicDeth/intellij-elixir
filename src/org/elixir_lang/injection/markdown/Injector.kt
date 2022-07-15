package org.elixir_lang.injection.markdown

import com.intellij.lang.injection.MultiHostInjector
import com.intellij.lang.injection.MultiHostRegistrar
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import org.elixir_lang.errorreport.Logger
import org.elixir_lang.psi.AtUnqualifiedNoParenthesesCall
import org.elixir_lang.psi.ElixirAtomKeyword
import org.elixir_lang.psi.Heredoc
import org.elixir_lang.psi.QuotableKeywordPair
import org.elixir_lang.reference.ModuleAttribute.Companion.DOCUMENTATION_NAME_SET
import org.intellij.plugins.markdown.lang.MarkdownLanguage
import java.lang.Integer.max

class Injector : MultiHostInjector {
    override fun getLanguagesToInject(registrar: MultiHostRegistrar, context: PsiElement) {
        context
            .let { it as? AtUnqualifiedNoParenthesesCall<*> }
            ?.takeIf(Companion::isValidHost)
            ?.lastChild
            ?.firstChild
            ?.firstChild
            ?.let { getLanguagesToInjectInQuote(registrar, it) }
    }

    private fun getLanguagesToInjectInQuote(registrar: MultiHostRegistrar, documentation: PsiElement) {
        when (documentation) {
            is Heredoc -> {
                registrar.startInjecting(MarkdownLanguage.INSTANCE)

                val prefixLength = documentation.heredocPrefix.textLength
                val quoteOffset = documentation.textOffset

                for (line in documentation.heredocLineList) {
                    val lineOffset = line.textOffset
                    val lineOffsetRelativeToQuote = lineOffset + prefixLength - quoteOffset
                    val lineMarkdownLength = max(line.textLength - prefixLength, 0)
                    val textRangeInQuote = TextRange.from(lineOffsetRelativeToQuote, lineMarkdownLength)

                    registrar.addPlace(null, null, documentation, textRangeInQuote)
                }

                registrar.doneInjecting()
            }
            is ElixirAtomKeyword -> Unit
            is QuotableKeywordPair -> {
                when (val key = documentation.keywordKey.text) {
                    "since" -> Unit
                    else -> {
                        Logger.error(
                            javaClass,
                            "Do not known whether to inject Markdown in documentation key $key",
                            documentation
                        )
                    }
                }
            }
            else -> {
                Logger.error(javaClass, "Do not know whether to inject Markdown in documentation", documentation)
            }
        }
    }

    override fun elementsToInjectIn(): List<Class<out PsiElement>> =
        listOf(AtUnqualifiedNoParenthesesCall::class.java)

    companion object {
        fun isValidHost(atUnqualifiedNoParenthesesCall: AtUnqualifiedNoParenthesesCall<*>): Boolean =
            atUnqualifiedNoParenthesesCall.atIdentifier.lastChild?.text in DOCUMENTATION_NAME_SET
    }
}