package org.elixir_lang.documentation

import com.intellij.lang.Language
import com.intellij.lang.documentation.DocumentationSettings
import com.intellij.lang.documentation.QuickDocHighlightingHelper
import com.intellij.openapi.diagnostic.ControlFlowException
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.richcopy.HtmlSyntaxInfoUtil
import com.intellij.openapi.project.Project
import com.intellij.util.concurrency.annotations.RequiresReadLock
import org.elixir_lang.ElixirLanguage

/**
 * Renders Elixir code blocks for documentation with syntax highlighting and semantic overlays.
 *
 * For Elixir code, uses [HtmlSyntaxInfoUtil.HtmlCodeSnippetBuilder] (`@ApiStatus.Experimental`)
 * with an additional [ElixirRenderedDocSemanticHighlighter] iterator to produce richer
 * highlighting than the lexer alone provides. Falls back to
 * [QuickDocHighlightingHelper.getStyledCodeBlock] on failure or for non-Elixir languages.
 *
 * The try/catch fallback is intentional: `HtmlCodeSnippetBuilder` is `@Experimental` and
 * may change between platform versions; the fallback ensures graceful degradation.
 */
@Suppress("UnstableApiUsage")
internal object RenderedDocCodeBlockRenderer {
    private val logger = Logger.getInstance(RenderedDocCodeBlockRenderer::class.java)

    /**
     * Renders [rawCode] as a syntax-highlighted HTML code block.
     *
     * @param project the current project
     * @param language the language to highlight as (Elixir gets semantic overlays; others
     *        fall through to the platform's default highlighting)
     * @param rawCode the raw code content extracted from the markdown AST
     * @return HTML string wrapped in `<pre><code>...</code></pre>` with inline styling
     */
    @RequiresReadLock
    fun render(project: Project, language: Language, rawCode: String): String {
        if (!DocumentationSettings.isHighlightingOfCodeBlocksEnabled() || language != ElixirLanguage) {
            return QuickDocHighlightingHelper.getStyledCodeBlock(project, language, rawCode)
        }

        val highlightedInput = highlightedInput(rawCode)
        val additionalIterator = ElixirRenderedDocSemanticHighlighter.additionalIterator(project, highlightedInput)
        val highlighted = StringBuilder()

        try {
            HtmlSyntaxInfoUtil.HtmlCodeSnippetBuilder(highlighted, project, language)
                .codeSnippet(highlightedInput)
                .doTrimIndent(false)
                .saturationFactor(DocumentationSettings.getHighlightingSaturation(true))
                .additionalIterator(additionalIterator)
                .build()
        } catch (exception: Exception) {
            if (exception is ControlFlowException) {
                throw exception
            }

            logger.warn("Falling back to QuickDocHighlightingHelper for rendered Elixir code block", exception)
            return QuickDocHighlightingHelper.getStyledCodeBlock(project, language, rawCode)
        }

        return buildString {
            append(QuickDocHighlightingHelper.CODE_BLOCK_PREFIX)
            append(highlighted)
            append(QuickDocHighlightingHelper.CODE_BLOCK_SUFFIX)
        }
    }

    private fun highlightedInput(rawCode: String): String =
        rawCode
            .trim('\n', '\r')
            .replace('\u00A0', ' ')
            .trimEnd()
            .trimIndent()
            .replace("\t", "    ")
}
