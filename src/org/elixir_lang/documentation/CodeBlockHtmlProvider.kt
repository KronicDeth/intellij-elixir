package org.elixir_lang.documentation

import com.intellij.openapi.project.Project
import org.elixir_lang.ElixirLanguage
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.getTextInNode
import org.intellij.markdown.html.GeneratingProvider
import org.intellij.markdown.html.HtmlGenerator

/**
 * HTML generating provider for indented code blocks (`CODE_BLOCK`) in rendered documentation.
 *
 * Extracts code content by iterating child nodes (following the platform's
 * `DocCodeBlockGeneratingProvider` pattern) and renders it with Elixir syntax highlighting
 * via [RenderedDocCodeBlockRenderer].
 *
 * Indented code blocks in Elixir documentation are always Elixir by convention,
 * so [ElixirLanguage] is passed unconditionally.
 */
class CodeBlockHtmlProvider(private val project: Project) : GeneratingProvider {
    override fun processNode(
        visitor: HtmlGenerator.HtmlGeneratingVisitor,
        text: String,
        node: ASTNode
    ) {
        val contents = StringBuilder()

        node.children.forEach { child ->
            when (child.type) {
                MarkdownTokenTypes.CODE_LINE,
                MarkdownTokenTypes.EOL -> contents.append(child.getTextInNode(text))
            }
        }

        val rawCode = contents.toString()

        visitor.consumeHtml(
            RenderedDocCodeBlockRenderer.render(project, ElixirLanguage, rawCode)
        )
    }
}
