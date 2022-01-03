package org.elixir_lang.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.project.DumbAware
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiRecursiveElementVisitor
import com.intellij.psi.tree.TokenSet
import org.elixir_lang.ElixirSyntaxHighlighter
import org.elixir_lang.ElixirSyntaxHighlighter.Companion.SIGIL
import org.elixir_lang.ElixirSyntaxHighlighter.Companion.SIGIL_BY_NAME
import org.elixir_lang.eex.Language
import org.elixir_lang.errorreport.Logger
import org.elixir_lang.psi.*
import org.elixir_lang.psi.CallDefinitionClause.`is`
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.name.Function
import org.elixir_lang.psi.call.name.Function.UNQUOTE_SPLICING
import org.elixir_lang.psi.call.name.Module
import org.elixir_lang.psi.call.name.Module.KERNEL
import org.elixir_lang.psi.impl.identifierName
import org.elixir_lang.psi.impl.stripAccessExpression
import org.elixir_lang.psi.operation.*
import org.elixir_lang.reference.ModuleAttribute.Companion.isCallbackName
import org.elixir_lang.reference.ModuleAttribute.Companion.isDocumentationName
import org.elixir_lang.reference.ModuleAttribute.Companion.isSpecificationName
import org.elixir_lang.reference.ModuleAttribute.Companion.isTypeName
import org.elixir_lang.structure_view.element.CallDefinitionHead.Companion.stripAllOuterParentheses

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
                            val node = quote.node
                            val textAttributesKey = if (quote.isCharList) {
                                ElixirSyntaxHighlighter.CHAR_LIST
                            } else {
                                ElixirSyntaxHighlighter.STRING
                            }

                            Highlighter.highlight(holder, node.firstChildNode, textAttributesKey)

                            visitChildren(quote, textAttributesKey)

                            Highlighter.highlight(holder, node.lastChildNode, textAttributesKey)
                        }


                        private fun visitHeredoc(heredoc: Heredoc, textAttributesKey: TextAttributesKey) {
                            heredoc.heredocLineList.forEach { heredocLine ->
                                visitBodied(heredocLine, textAttributesKey)
                            }
                        }

                        private fun visitBodied(bodied: Bodied, textAttributesKey: TextAttributesKey) {
                            visitBody(bodied.body, textAttributesKey)
                        }

                        private fun visitBody(body: Body, textAttributesKey: TextAttributesKey) {
                            body.node.getChildren(TokenSet.create(ElixirTypes.FRAGMENT, ElixirTypes.LINE_TERMINATOR, ElixirTypes.HEREDOC_TERMINATOR)).forEach { fragment ->
                                Highlighter.highlight(holder, fragment, textAttributesKey)
                            }
                            body.node.getChildren(TokenSet.create(ElixirTypes.ESCAPED_LINE_TERMINATOR, ElixirTypes.ESCAPED_HEREDOC_TERMINATOR)).forEach { escapedTerminator ->
                                Highlighter.highlight(holder, escapedTerminator, ElixirSyntaxHighlighter.VALID_ESCAPE_SEQUENCE)
                            }
                        }

                        override fun visitSigil(sigil: Sigil) {
                            val node = sigil.node
                            val tilde = node.firstChildNode
                            val sigilName = tilde.treeNext
                            val textAttributesKey = textAttributesKey(sigilName.text)
                            val promoter = sigilName.treeNext

                            Highlighter.highlight(holder, tilde, textAttributesKey)
                            Highlighter.highlight(holder, sigilName, textAttributesKey)
                            Highlighter.highlight(holder, promoter, textAttributesKey)

                            visitChildren(sigil, textAttributesKey)

                            val sigilModifiers = node.lastChildNode
                            val terminator = sigilModifiers.treePrev

                            Highlighter.highlight(holder, terminator, textAttributesKey)
                            Highlighter.highlight(holder, sigilModifiers, textAttributesKey)
                        }

                        private fun textAttributesKey(sigilName: String): TextAttributesKey =
                            SIGIL_BY_NAME[sigilName.first()] ?: SIGIL

                        private fun visitChildren(parent: PsiElement, textAttributesKey: TextAttributesKey) =
                                when (parent) {
                                    is Heredoc -> visitHeredoc(parent, textAttributesKey)
                                    is Line -> visitBodied(parent, textAttributesKey)
                                    else -> Unit
                                }
                    }
            )
        }
}
