package org.elixir_lang.injection

import com.intellij.lang.Language
import com.intellij.lang.html.HTMLLanguage
import com.intellij.lang.injection.MultiHostInjector
import com.intellij.lang.injection.MultiHostRegistrar
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import org.elixir_lang.psi.SigilHeredoc
import org.elixir_lang.psi.SigilLine
import java.util.regex.Pattern
import org.elixir_lang.eex.Language as EexLanguage

class ElixirSigilInjector : MultiHostInjector {
    override fun getLanguagesToInject(registrar: MultiHostRegistrar, context: PsiElement) {
        when (context) {
            is SigilLine -> handleSigilLine(registrar, context)
            is SigilHeredoc -> handleSigilHeredoc(registrar, context)
            else -> return
        }
    }

    private fun handleSigilLine(registrar: MultiHostRegistrar, sigilLine: SigilLine) {
        if (!sigilLine.isValidHost) return

        sigilLine.body?.let { lineBody ->
            val lang = languageForSigil(sigilLine.sigilName())
            if (lang != null) {
                this.thisLogger().info("handleSigilLine - lineBody.textRangeInParent: ${lineBody.textRangeInParent}")
                registrar
                    .startInjecting(lang)
                    .addPlace(null, null, sigilLine, lineBody.textRangeInParent)
                    .doneInjecting()
            }
        }
    }

    private fun handleSigilHeredoc(registrar: MultiHostRegistrar, sigilHeredoc: SigilHeredoc) {
        if (!sigilHeredoc.isValidHost) return

        val lang = languageForSigil(sigilHeredoc.sigilName()) ?: return

        // Find injectable content first
        val injectableRanges = findInjectableRanges(sigilHeredoc)
        if (injectableRanges.isEmpty()) return

        // Only start injection if we have content to inject
        registrar.startInjecting(lang)
        for (range in injectableRanges) {
            registrar.addPlace(null, null, sigilHeredoc, range)
        }
        registrar.doneInjecting()
    }

    private fun findInjectableRanges(sigilHeredoc: SigilHeredoc): List<TextRange> {
        val ranges = mutableListOf<TextRange>()
        val prefixLength = sigilHeredoc.heredocPrefix.textLength
        val quoteOffset = sigilHeredoc.textOffset
        var listIndent = -1
        var inException = false

        for (line in sigilHeredoc.heredocLineList) {
            val lineTextLength = line.textLength
            val lineText = line.text

            if (lineTextLength <= prefixLength) continue

            val lineMarkdownText = lineText.substring(prefixLength)
            val lineOffset = line.textOffset
            val lineOffsetRelativeToQuote = lineOffset - quoteOffset
            val markdownOffsetRelativeToQuote = lineOffsetRelativeToQuote + prefixLength

            val listStartMatcher = LIST_START_PATTERN.matcher(lineMarkdownText)

            when {
                listStartMatcher.matches() -> {
                    listIndent = listStartMatcher.group("indent").length
                }

                else -> {
                    if (listIndent > 0) {
                        val indentedMatcher = INDENTED_PATTERN.matcher(lineMarkdownText)
                        if (indentedMatcher.matches() && indentedMatcher.group("indent").length < listIndent + 1) {
                            listIndent = -1
                        }
                    }

                    if (listIndent == -1 && lineMarkdownText.startsWith(CODE_BLOCK_INDENT)) {
                        val lineCodeText = lineMarkdownText.substring(CODE_BLOCK_INDENT_LENGTH)
                        val codeOffsetRelativeToQuote = markdownOffsetRelativeToQuote + CODE_BLOCK_INDENT_LENGTH

                        when {
                            lineCodeText.startsWith(EXCEPTION_PREFIX) -> inException = true
                            lineCodeText.startsWith(DEBUG_PREFIX) -> inException = false
                            else -> {
                                val (lineElixirText, elixirOffsetRelativeToQuote) = when {
                                    lineCodeText.startsWith(IEX_PROMPT) -> {
                                        inException = false
                                        Pair<String, Int>(
                                            lineCodeText.substring(IEX_PROMPT_LENGTH),
                                            codeOffsetRelativeToQuote + IEX_PROMPT_LENGTH
                                        )
                                    }

                                    lineCodeText.startsWith(IEX_CONTINUATION) -> {
                                        inException = false
                                        Pair<String, Int>(
                                            lineCodeText.substring(IEX_CONTINUATION_LENGTH),
                                            codeOffsetRelativeToQuote + IEX_CONTINUATION_LENGTH
                                        )
                                    }

                                    else -> Pair(lineCodeText, codeOffsetRelativeToQuote)
                                }

                                if (!inException) {
                                    ranges.add(TextRange.from(elixirOffsetRelativeToQuote, lineElixirText.length))
                                }
                            }
                        }
                    }
                }
            }
        }

        return ranges
    }

    override fun elementsToInjectIn(): List<Class<out PsiElement>> {
        return listOf(SigilLine::class.java, SigilHeredoc::class.java)
    }

    private fun languageForSigil(sigilName: Char): Language? {
        return when (sigilName) {
            'H' -> HTMLLanguage.INSTANCE
            'L' -> EexLanguage.INSTANCE
            else -> null
        }
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
