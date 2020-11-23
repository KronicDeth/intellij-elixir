package org.elixir_lang.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.project.DumbAware
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiRecursiveElementVisitor
import org.elixir_lang.ElixirSyntaxHighlighter
import org.elixir_lang.psi.ElixirMapArguments
import org.elixir_lang.psi.ElixirMapOperation
import org.elixir_lang.psi.ElixirStructOperation

/**
 * Annotates maps and structs
 */
class Map : Annotator, DumbAware {
    /*
     * Public Instance Methods
     */
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
                object : PsiRecursiveElementVisitor() {
                    /*
                    * Public Instance Methods
                    */
                    override fun visitElement(element: PsiElement) {
                        if (element is ElixirMapOperation) {
                            visitMapOperation(element)
                        } else if (element is ElixirStructOperation) {
                            visitStructOperation(element)
                        }
                    }

                    /*
                    * Private Instance Methods
                    */
                    private fun visitMapOperation(mapOperation: ElixirMapOperation) {
                        highlight(holder, mapOperation.mapPrefixOperator, ElixirSyntaxHighlighter.MAP)
                        highlight(holder, mapOperation.mapArguments, ElixirSyntaxHighlighter.MAP)
                    }

                    private fun visitStructOperation(structOperation: ElixirStructOperation) {
                        highlight(holder, structOperation.mapPrefixOperator, ElixirSyntaxHighlighter.STRUCT)
                        /* DO NOT highlight mapExpression.  It will be highlighted as either an alias or module
                          attribute, which are more specific and useful. */
                        highlight(holder, structOperation.mapArguments, ElixirSyntaxHighlighter.STRUCT)
                    }
                }
        )
    }

    /*
     * Private Instance Methods
     */

    private fun highlight(annotationHolder: AnnotationHolder,
                          mapArguments: ElixirMapArguments,
                          textAttributesKey: TextAttributesKey) {
        val braces = mapArguments.node.getChildren(ElixirSyntaxHighlighter.BRACES_TOKEN_SET)
        for (brace in braces) {
            Highlighter.highlight(annotationHolder, brace, textAttributesKey)
        }
    }

    private fun highlight(annotationHolder: AnnotationHolder,
                          element: PsiElement,
                          textAttributesKey: TextAttributesKey) {
        Highlighter.highlight(annotationHolder, element, textAttributesKey)
    }
}
