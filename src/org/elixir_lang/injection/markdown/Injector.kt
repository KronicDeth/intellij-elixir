package org.elixir_lang.injection.markdown

import com.intellij.lang.injection.MultiHostInjector
import com.intellij.lang.injection.MultiHostRegistrar
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import org.elixir_lang.ElixirLanguage
import org.elixir_lang.errorreport.Logger
import org.elixir_lang.psi.AtUnqualifiedNoParenthesesCall
import org.elixir_lang.psi.ElixirAtomKeyword
import org.elixir_lang.psi.Heredoc
import org.elixir_lang.psi.QuotableKeywordPair
import org.elixir_lang.reference.ModuleAttribute.Companion.DOCUMENTATION_NAME_SET
import org.intellij.plugins.markdown.lang.MarkdownLanguage

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
                injectMarkdownInQuote(registrar, documentation)
                injectElixirInCodeBlocksInQuote(registrar, documentation)
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

    private fun injectMarkdownInQuote(registrar: MultiHostRegistrar, documentation: Heredoc) {
        registrar.startInjecting(MarkdownLanguage.INSTANCE)

        val prefixLength = documentation.heredocPrefix.textLength
        val quoteOffset = documentation.textOffset

        for (line in documentation.heredocLineList) {
            val lineTextLength = line.textLength
            val lineText = line.text

            // > to include newline
            if (lineTextLength > prefixLength) {
                val lineMarkdownText = lineText.substring(prefixLength)

                val lineOffset = line.textOffset
                val markdownOffsetRelativeToQuote = lineOffset + prefixLength - quoteOffset
                val lineMarkdownTextLength = if (lineMarkdownText.startsWith(CODE_BLOCK_INDENT)) {
                    val lineCodeText = lineMarkdownText.substring(CODE_BLOCK_INDENT_LENGTH)

                    if (lineCodeText.startsWith(IEX_PROMPT)) {
                        CODE_BLOCK_INDENT_LENGTH + IEX_PROMPT_LENGTH
                    } else {
                        CODE_BLOCK_INDENT_LENGTH
                    }
                } else {
                    lineMarkdownText.length
                }

                val textRangeInQuote = TextRange.from(markdownOffsetRelativeToQuote, lineMarkdownTextLength)

                registrar.addPlace(null, null, documentation, textRangeInQuote)
            }
        }

        registrar.doneInjecting()
    }

    private fun injectElixirInCodeBlocksInQuote(registrar: MultiHostRegistrar, documentation: Heredoc) {
        registrar.startInjecting(MarkdownLanguage.INSTANCE)

        val prefixLength = documentation.heredocPrefix.textLength
        val quoteOffset = documentation.textOffset
        var inCodeBlock = false

        for (line in documentation.heredocLineList) {
            val lineTextLength = line.textLength
            val lineText = line.text

            // > to include newline
            if (lineTextLength > prefixLength) {
                val lineMarkdownText = lineText.substring(prefixLength)

                val lineOffset = line.textOffset
                val lineOffsetRelativeToQuote = lineOffset - quoteOffset
                val markdownOffsetRelativeToQuote = lineOffsetRelativeToQuote + prefixLength

                if (lineMarkdownText.startsWith(CODE_BLOCK_INDENT)) {
                    val lineCodeText = lineMarkdownText.substring(CODE_BLOCK_INDENT_LENGTH)
                    val codeOffsetRelativeToQuote = markdownOffsetRelativeToQuote + CODE_BLOCK_INDENT_LENGTH

                    val (lineElixirText, elixirOffsetRelativeToQuote) = if (lineCodeText.startsWith(IEX_PROMPT)) {
                        Pair(lineCodeText.substring(IEX_PROMPT_LENGTH), codeOffsetRelativeToQuote + IEX_PROMPT_LENGTH)
                    } else {
                        Pair(lineCodeText, codeOffsetRelativeToQuote)
                    }

                    val textRangeInQuote = TextRange.from(elixirOffsetRelativeToQuote, lineElixirText.length)

                    if (!inCodeBlock) {
                        registrar.startInjecting(ElixirLanguage)

                        inCodeBlock = true
                    }

                    registrar.addPlace(null, null, documentation, textRangeInQuote)
                } else if (lineMarkdownText.isNotBlank()) {
                    if (inCodeBlock) {
                        registrar.doneInjecting()

                        inCodeBlock = false
                    }
                }
            }
        }

        if (inCodeBlock) {
            registrar.doneInjecting()
        }
    }

    override fun elementsToInjectIn(): List<Class<out PsiElement>> =
        listOf(AtUnqualifiedNoParenthesesCall::class.java)

    companion object {
        private const val CODE_BLOCK_INDENT = "    "
        private const val CODE_BLOCK_INDENT_LENGTH = CODE_BLOCK_INDENT.length
        private const val IEX_PROMPT = "iex> "
        private const val IEX_PROMPT_LENGTH = IEX_PROMPT.length

        fun isValidHost(atUnqualifiedNoParenthesesCall: AtUnqualifiedNoParenthesesCall<*>): Boolean =
            atUnqualifiedNoParenthesesCall.atIdentifier.lastChild?.text in DOCUMENTATION_NAME_SET
    }
}
