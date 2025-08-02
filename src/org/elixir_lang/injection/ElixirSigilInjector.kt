package org.elixir_lang.injection

import com.intellij.lang.Language
import com.intellij.lang.injection.MultiHostInjector
import com.intellij.lang.injection.MultiHostRegistrar
import com.intellij.lang.html.HTMLLanguage;
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import org.elixir_lang.eex.Language as EexLanguage;
import org.elixir_lang.psi.*
import java.util.regex.Pattern

class ElixirSigilInjector : MultiHostInjector {
    override fun getLanguagesToInject(registrar: MultiHostRegistrar, context: PsiElement) {
        val sigilLine = context as? SigilLine
        val sigilHeredoc = context as? SigilHeredoc

        if (sigilLine != null && sigilLine.isValidHost()) {
            sigilLine.body?.let { lineBody ->
                    val lang = languageForSigil(sigilLine.sigilName());
                if (lang != null) {
                    registrar.startInjecting(lang)
                    registrar.addPlace(null, null, sigilLine, lineBody.textRangeInParent)
                    registrar.doneInjecting()
                }
            }
        } else if (sigilHeredoc != null && sigilHeredoc.isValidHost()) {
            val prefixLength = sigilHeredoc.heredocPrefix.textLength
            val quoteOffset = sigilHeredoc.textOffset
            var inCodeBlock = false
            var listIndent = -1
            var inException = false

            for (line in sigilHeredoc.heredocLineList) {
                val lineTextLength = line.textLength
                val lineText = line.text

                // > to include newline
                if (lineTextLength > prefixLength) {
                    val lineMarkdownText = lineText.substring(prefixLength)

                    val lineOffset = line.textOffset
                    val lineOffsetRelativeToQuote = lineOffset - quoteOffset
                    val markdownOffsetRelativeToQuote = lineOffsetRelativeToQuote + prefixLength

                    val listStartMatcher = LIST_START_PATTERN.matcher(lineMarkdownText)

                    if (listStartMatcher.matches()) {
                        listIndent = listStartMatcher.group("indent").length

                        if (inCodeBlock) {
                            registrar.doneInjecting()

                            inCodeBlock = false
                        }
                    } else {
                        if (listIndent > 0) {
                            val indentedMatcher = INDENTED_PATTERN.matcher(lineMarkdownText)

                            if (indentedMatcher.matches() && indentedMatcher.group("indent").length < listIndent + 1) {
                                listIndent = -1
                            }
                        }

                        if (listIndent == -1) {
                            if (lineMarkdownText.startsWith(CODE_BLOCK_INDENT)) {
                                val lineCodeText = lineMarkdownText.substring(CODE_BLOCK_INDENT_LENGTH)
                                val codeOffsetRelativeToQuote = markdownOffsetRelativeToQuote + CODE_BLOCK_INDENT_LENGTH

                                if (lineCodeText.startsWith(EXCEPTION_PREFIX)) {
                                    inException = true
                                } else if (lineCodeText.startsWith(DEBUG_PREFIX)) {
                                    inException = false
                                } else {
                                    val (lineElixirText, elixirOffsetRelativeToQuote) = when {
                                        lineCodeText.startsWith(IEX_PROMPT) -> {
                                            inException = false

                                            Pair(
                                                    lineCodeText.substring(IEX_PROMPT_LENGTH),
                                                    codeOffsetRelativeToQuote + IEX_PROMPT_LENGTH
                                            )
                                        }

                                        lineCodeText.startsWith(IEX_CONTINUATION) -> {
                                            inException = false

                                            Pair(
                                                    lineCodeText.substring(IEX_CONTINUATION_LENGTH),
                                                    codeOffsetRelativeToQuote + IEX_CONTINUATION_LENGTH
                                            )
                                        }

                                        else -> {
                                            Pair(lineCodeText, codeOffsetRelativeToQuote)
                                        }
                                    }

                                    if (!inException) {
                                        val textRangeInQuote =
                                                TextRange.from(elixirOffsetRelativeToQuote, lineElixirText.length)

                                        val lang = languageForSigil(sigilHeredoc.sigilName());
                                        if (!inCodeBlock && lang != null) {
                                            registrar.startInjecting(lang)

                                            inCodeBlock = true
                                        }

                                        registrar.addPlace(null, null, sigilHeredoc, textRangeInQuote)
                                    }
                                }
                            } else if (lineMarkdownText.isNotBlank()) {
                                if (inCodeBlock) {
                                    registrar.doneInjecting()

                                    inCodeBlock = false
                                    inException = false
                                }
                            }
                        }
                    }
                }
            }

            if (inCodeBlock) {
                registrar.doneInjecting()
            }

        } else {
            for (child in context.children) {
                getLanguagesToInject(registrar, child)
            }
        }
    }

    override fun elementsToInjectIn(): List<Class<out PsiElement>> {
        return listOf(PsiElement::class.java)
    }

    fun languageForSigil(sigilName: Char): Language? {
    if (sigilName == 'H') {
        return HTMLLanguage.INSTANCE
    } else if (sigilName == 'L') {
        return EexLanguage.INSTANCE
    }

    return null
    }

    companion object {
        private const val CODE_BLOCK_INDENT = "    "
        private const val CODE_BLOCK_INDENT_LENGTH = CODE_BLOCK_INDENT.length
        private const val IEX_PROMPT = "iex> "
        private const val IEX_PROMPT_LENGTH = IEX_PROMPT.length
        private const val IEX_CONTINUATION = "...> "
        private const val IEX_CONTINUATION_LENGTH = IEX_CONTINUATION.length
        private const val EXCEPTION_PREFIX = "** ("
        private const val DEBUG_PREFIX = "*DBG* "
        private val LIST_START_PATTERN = Pattern.compile("(?<indent>\\s*)([-*+]|\\d+\\.) \\S+.*\n")
        private val INDENTED_PATTERN = Pattern.compile("(?<indent>\\s*).*\n")
    }
}
