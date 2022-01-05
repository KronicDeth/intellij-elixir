package org.elixir_lang.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.project.DumbAware
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.TokenSet
import org.elixir_lang.ElixirSyntaxHighlighter
import org.elixir_lang.ElixirSyntaxHighlighter.Companion.SIGIL
import org.elixir_lang.ElixirSyntaxHighlighter.Companion.SIGIL_BY_NAME
import org.elixir_lang.psi.*
import org.elixir_lang.psi.impl.identifierName
import org.elixir_lang.psi.operation.*
import org.elixir_lang.reference.ModuleAttribute.Companion.isDocumentationName

/**
 * Annotates quotes and sigils
 */
class Textual : Annotator, DumbAware {
    /**
     * Annotates the specified PSI element.
     * It is guaranteed to be executed in non-reentrant fashion.
     * I.e there will be no call of this method for this instance before previous call get completed.
     * Multiple instances of the annotator might exist simultaneously, though.
     *
     * @param element to annotate.
     * @param holder  the container which receives annotations created by the plugin.
     */
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
            element.accept(
                    object : ElixirVisitor() {
                        override fun visitQuote(quote: Quote) {
                            if (!isDocumentationText(quote)) {
                                val textAttributesKey = if (quote.isCharList) {
                                    ElixirSyntaxHighlighter.CHAR_LIST
                                } else {
                                    ElixirSyntaxHighlighter.STRING
                                }

                                highlightQuote(holder, quote, textAttributesKey)
                            }
                        }

                        override fun visitSigil(sigil: Sigil) {
                            if (!isDocumentationText(sigil)) {
                                val textAttributesKey = textAttributesKey(sigil.sigilName())

                                highlightSigil(holder, sigil, textAttributesKey)
                            }
                        }

                        private fun isDocumentationText(parent: Parent): Boolean =
                            parent
                                    .parent?.let { it as? ElixirAccessExpression }
                                    ?.parent?.let { it as? ElixirNoParenthesesOneArgument }
                                    ?.parent?.let { it as? AtUnqualifiedNoParenthesesCall<*> }
                                    ?.atIdentifier
                                    ?.identifierName()?.let { isDocumentationName(it) }
                                    ?: false

                        private fun textAttributesKey(sigilName: Char): TextAttributesKey =
                            SIGIL_BY_NAME[sigilName] ?: SIGIL
                    }
            )
        }

    companion object {
        fun highlightQuote(holder: AnnotationHolder, quote: Quote, textAttributesKey: TextAttributesKey) {
            val node = quote.node

            Highlighter.highlight(holder, node.firstChildNode, textAttributesKey)

            highlightChildren(holder, quote, textAttributesKey)

            Highlighter.highlight(holder, node.lastChildNode, textAttributesKey)
        }

        fun highlightSigil(holder: AnnotationHolder, sigil: Sigil, textAttributesKey: TextAttributesKey) {
            val node = sigil.node

            val tilde = node.firstChildNode
            Highlighter.highlight(holder, tilde, textAttributesKey)

            val sigilName = tilde.treeNext
            Highlighter.highlight(holder, sigilName, textAttributesKey)

            val promoter = sigilName.treeNext
            Highlighter.highlight(holder, promoter, textAttributesKey)

            highlightChildren(holder, sigil, textAttributesKey)

            val sigilModifiers = node.lastChildNode
            val terminator = sigilModifiers.treePrev

            Highlighter.highlight(holder, terminator, textAttributesKey)
            Highlighter.highlight(holder, sigilModifiers, textAttributesKey)
        }

        private fun highlightChildren(holder: AnnotationHolder, parent: PsiElement, textAttributesKey: TextAttributesKey) =
                when (parent) {
                    is Heredoc -> highlightHeredoc(holder, parent, textAttributesKey)
                    is Line -> highlightBodied(holder, parent, textAttributesKey)
                    else -> Unit
                }

        private fun highlightHeredoc(holder: AnnotationHolder, heredoc: Heredoc, textAttributesKey: TextAttributesKey) {
            heredoc.heredocLineList.forEach { heredocLine ->
                highlightBodied(holder, heredocLine, textAttributesKey)
            }
        }

        private fun highlightBodied(holder: AnnotationHolder, bodied: Bodied, textAttributesKey: TextAttributesKey) {
            highlightBody(holder, bodied.body, textAttributesKey)
        }

        private fun highlightBody(holder: AnnotationHolder, body: Body, textAttributesKey: TextAttributesKey) {
            body.node.getChildren(TokenSet.create(ElixirTypes.FRAGMENT, ElixirTypes.LINE_TERMINATOR, ElixirTypes.HEREDOC_TERMINATOR)).forEach { fragment ->
                Highlighter.highlight(holder, fragment, textAttributesKey)
            }
            body.node.getChildren(TokenSet.create(ElixirTypes.ESCAPED_LINE_TERMINATOR, ElixirTypes.ESCAPED_HEREDOC_TERMINATOR)).forEach { escapedTerminator ->
                Highlighter.highlight(holder, escapedTerminator, ElixirSyntaxHighlighter.VALID_ESCAPE_SEQUENCE)
            }
        }
    }
}
