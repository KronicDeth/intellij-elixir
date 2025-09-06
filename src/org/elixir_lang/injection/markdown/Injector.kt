package org.elixir_lang.injection.markdown

import com.intellij.lang.injection.MultiHostInjector
import com.intellij.lang.injection.MultiHostRegistrar
import com.intellij.openapi.diagnostic.RuntimeExceptionWithAttachments
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.util.registry.Registry
import com.intellij.psi.PsiElement
import org.elixir_lang.ElixirLanguage
import org.elixir_lang.psi.*
import org.elixir_lang.psi.impl.stripAccessExpression
import org.elixir_lang.reference.ModuleAttribute.Companion.DOCUMENTATION_NAME_SET
import org.intellij.plugins.markdown.lang.MarkdownLanguage
import java.util.regex.Pattern

class Injector : MultiHostInjector {
    override fun getLanguagesToInject(registrar: MultiHostRegistrar, context: PsiElement) {
        context
            .let { it as? AtUnqualifiedNoParenthesesCall<*> }
            ?.takeIf(Companion::isValidHost)
            ?.lastChild
            ?.firstChild
            ?.firstChild
            ?.stripAccessExpression()
            ?.let { getLanguagesToInjectInQuote(registrar, it) }
    }

    /**
     * Returns true if we should inject into literal sigils (~S, ~W, …).
     * By default this is **disabled** to avoid SmartPsiElementPointer issues in IntelliJ 2025.1+.
     * Can be enabled via Registry for testing or if using older IntelliJ versions.
     */
    private fun literalSigilInjectionEnabled(): Boolean =
        Registry.`is`(REG_KEY_ENABLE_LITERAL_SIGIL_INJECTION, true)

    private fun getLanguagesToInjectInQuote(registrar: MultiHostRegistrar, documentation: PsiElement) {
        when (documentation) {
            is SigilHeredoc -> {
                // Handle sigil heredocs (like ~s"""...""" or ~S"""...""")
                val isLiteral = isLiteralSigil(documentation)

                if (isLiteral) {
                    // Literal sigils (~S""") - inject with caution due to IntelliJ 2025.1+ issues
                    val injectionEnabled = literalSigilInjectionEnabled()

                    if (injectionEnabled) {
                        // If enabled via Registry, inject Markdown only (no nested Elixir)
                        injectMarkdownInQuote(registrar, documentation)
                    }
                    // If disabled, no injection occurs to avoid SmartPsiElementPointer crashes
                } else {
                    // Interpolating sigils (~s""") - inject Elixir code blocks only
                    injectElixirInCodeBlocksInQuote(registrar, documentation)
                }
            }

            is Heredoc -> {
                // Handle regular heredocs (like """...""" or '''...''')
                injectMarkdownInQuote(registrar, documentation)
                injectElixirInCodeBlocksInQuote(registrar, documentation)
            }

            // Issue #2923 associated bug.  @moduledoc on unquote
            //    #
            //    # * We inline most of the code for performance, so it is specific
            //    #   per helper module anyway.
            //    #
            //    code = quote do
            //      @moduledoc unquote(docs) && """
            //      Module with named helpers generated from #{inspect unquote(env.module)}.
            //      """
            //      unquote(defhelper)
            //      unquote(defcatch_all)
            //      unquote_splicing(impls)
            is ElixirMatchedUnqualifiedParenthesesCall,
            is ElixirAlias,
                // @external_resource "README.md"
                // @moduledoc @external_resource
                //            |> File.read!()
                //            |> String.split("<!-- MDOC !-->")
                //            |> Enum.fetch!(1)
            is ElixirMatchedArrowOperation,
                // With missing quotes like in #2991 `@module implements logic`
            is ElixirIdentifier,
            is ElixirAtomKeyword -> Unit

            is ElixirLine -> injectMarkdownInQuote(registrar, documentation)
            is QuotableKeywordPair -> {
                when (val key = documentation.keywordKey.text) {
                    "deprecated" -> getLanguagesToInjectInQuote(registrar, documentation.keywordValue)
                    "authors", "group", "guard", "request_body", "responses", "since", "type" -> Unit
                    else -> {
                        LOGGER.error(
                            "Do not known whether to inject Markdown in documentation key $key",
                        )
                    }
                }
            }

            else -> {
                LOGGER.error("Do not know whether to inject Markdown in documentation")
            }
        }
    }

    private fun injectMarkdownInQuote(registrar: MultiHostRegistrar, documentation: Heredoc) {
        var injectionStarted = false
        val prefixLength = documentation.heredocPrefix?.textLength ?: 0
        val quoteOffset = documentation.textOffset
        var listIndent = -1
        var inException = false

        for (line in documentation.heredocLineList) {
            val lineTextLength = line.textLength
            val lineText = line.text

            // > to include newline
            if (lineTextLength > prefixLength) {
                val lineMarkdownText = lineText.substring(prefixLength)

                val lineOffset = line.textOffset
                val markdownOffsetRelativeToQuote = lineOffset + prefixLength - quoteOffset

                val listStartMatcher = LIST_START_PATTERN.matcher(lineMarkdownText)

                val lineMarkdownTextLength = if (listStartMatcher.matches()) {
                    listIndent = listStartMatcher.group("indent").length

                    lineMarkdownText.length
                } else {
                    if (listIndent > 0) {
                        val indentedMatcher = INDENTED_PATTERN.matcher(lineMarkdownText)

                        if (indentedMatcher.matches() && indentedMatcher.group("indent").length < listIndent + 1) {
                            listIndent = -1
                        }
                    }

                    if (listIndent > 0) {
                        lineMarkdownText.length
                    } else {
                        if (lineMarkdownText.startsWith(CODE_BLOCK_INDENT)) {
                            val lineCodeText = lineMarkdownText.substring(CODE_BLOCK_INDENT_LENGTH)

                            when {
                                lineCodeText.startsWith(IEX_PROMPT) -> {
                                    inException = false

                                    CODE_BLOCK_INDENT_LENGTH + IEX_PROMPT_LENGTH
                                }

                                lineCodeText.startsWith(IEX_CONTINUATION) -> {
                                    CODE_BLOCK_INDENT_LENGTH + IEX_CONTINUATION_LENGTH
                                }

                                lineCodeText.startsWith(DEBUG_PREFIX) -> {
                                    inException = false

                                    lineMarkdownText.length
                                }

                                lineCodeText.startsWith(EXCEPTION_PREFIX) -> {
                                    inException = true

                                    lineMarkdownText.length
                                }

                                else -> {
                                    if (inException) {
                                        lineMarkdownText.length
                                    } else {
                                        CODE_BLOCK_INDENT_LENGTH
                                    }
                                }
                            }
                        } else {
                            inException = false

                            lineMarkdownText.length
                        }
                    }
                }

                val textRangeInQuote = TextRange.from(markdownOffsetRelativeToQuote, lineMarkdownTextLength)

                if (!injectionStarted) {
                    registrar.startInjecting(MarkdownLanguage.INSTANCE)
                    injectionStarted = true
                }

                try {
                    registrar.addPlace(null, null, documentation, textRangeInQuote)
                } catch (exception: RuntimeExceptionWithAttachments) {
                    LOGGER.error("Cannot inject markdown in Heredoc - exception: $exception", exception)
                }
            }
        }

        if (injectionStarted) {
            registrar.doneInjecting()
        }
    }

    private fun injectMarkdownInQuote(registrar: MultiHostRegistrar, documentation: ElixirLine) {
        documentation.lineBody?.let { lineBody ->
            registrar.startInjecting(MarkdownLanguage.INSTANCE)
            registrar.addPlace(null, null, documentation, lineBody.textRangeInParent)
            registrar.doneInjecting()
        }
    }


    private fun injectElixirInCodeBlocksInQuote(registrar: MultiHostRegistrar, documentation: Heredoc) {
        registrar.startInjecting(MarkdownLanguage.INSTANCE)

        val prefixLength = documentation.heredocPrefix?.textLength ?: 0
        val quoteOffset = documentation.textOffset
        var inCodeBlock = false
        var listIndent = -1
        var inException = false

        for (line in documentation.heredocLineList) {
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

                                    if (!inCodeBlock) {
                                        registrar.startInjecting(ElixirLanguage)

                                        inCodeBlock = true
                                    }

                                    registrar.addPlace(null, null, documentation, textRangeInQuote)
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
    }

    override fun elementsToInjectIn(): List<Class<out PsiElement>> =
        listOf(AtUnqualifiedNoParenthesesCall::class.java)

    companion object {
        private val LOGGER = com.intellij.openapi.diagnostic.Logger.getInstance(Injector::class.java)
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

        fun isValidHost(atUnqualifiedNoParenthesesCall: AtUnqualifiedNoParenthesesCall<*>): Boolean =
            atUnqualifiedNoParenthesesCall.atIdentifier.lastChild?.text in DOCUMENTATION_NAME_SET

        /**
         * Registry key to control literal sigil injection behavior.
         *
         * Path: Help -> Find Action… -> "Registry…" -> search for this key name
         */
        const val REG_KEY_ENABLE_LITERAL_SIGIL_INJECTION =
            "org.elixir_lang.injection.enableLiteralSigilInjection"

        private fun isLiteralSigil(heredoc: Heredoc): Boolean {
            val prefix = heredoc.heredocPrefix?.text ?: return false
            return prefix.length >= 2 && prefix[0] == '~' && prefix[1].isUpperCase()
        }
    }
}
