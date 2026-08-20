package org.elixir_lang.documentation

import com.intellij.lang.documentation.QuickDocHighlightingHelper.guessLanguage
import com.intellij.openapi.project.Project
import org.elixir_lang.ElixirLanguage
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.getTextInNode
import org.intellij.markdown.html.GeneratingProvider
import org.intellij.markdown.html.HtmlGenerator

/**
 * HTML generating provider for fenced code blocks (`CODE_FENCE`) in rendered documentation.
 *
 * Extracts the language hint from the `FENCE_LANG` token and resolves it via
 * [guessLanguage]. Falls back to [ElixirLanguage] when no
 * language is specified or when the hint is unrecognized. Renders the code content
 * with syntax highlighting via [RenderedDocCodeBlockRenderer].
 */
class CodeFenceHtmlProvider(private val project: Project) : GeneratingProvider {
    override fun processNode(
        visitor: HtmlGenerator.HtmlGeneratingVisitor,
        text: String,
        node: ASTNode
    ) {
        val contents = StringBuilder()
        var languageId: String? = null

        node.children.forEach { child ->
            when (child.type) {
                MarkdownTokenTypes.CODE_FENCE_CONTENT,
                MarkdownTokenTypes.CODE_LINE,
                MarkdownTokenTypes.EOL -> contents.append(child.getTextInNode(text))
                MarkdownTokenTypes.FENCE_LANG -> {
                    languageId = HtmlGenerator
                        .leafText(text, child)
                        .toString()
                        .trim()
                        .takeWhile { !it.isWhitespace() }
                }
            }
        }

        val resolvedLanguage = guessLanguage(languageId) ?: ElixirLanguage
        val rawCode = contents.toString()

        visitor.consumeHtml(
            RenderedDocCodeBlockRenderer.render(project, resolvedLanguage, rawCode)
        )
    }
}
