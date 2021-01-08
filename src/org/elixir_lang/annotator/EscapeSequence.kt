package org.elixir_lang.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.openapi.project.DumbAware
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiRecursiveElementVisitor
import org.elixir_lang.ElixirSyntaxHighlighter
import org.elixir_lang.annotator.Highlighter.highlight
import org.elixir_lang.psi.EscapeSequence

/**
 * Annotates [org.elixir_lang.psi.EscapeSequence] as [ElixirSyntaxHighlighter.VALID_ESCAPE_SEQUENCE]
 */
class EscapeSequence : Annotator, DumbAware {
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
                        if (element is EscapeSequence) {
                            visitEscapeSequence(element)
                        }
                    }

                    /*
                    * Private Instance Methods
                    */

                    private fun visitEscapeSequence(escapeSequence: EscapeSequence) {
                        // parent can highlight itself
                        if (escapeSequence.parent !is EscapeSequence) {
                            highlight(holder, ElixirSyntaxHighlighter.VALID_ESCAPE_SEQUENCE)
                        }
                    }
                }
        )
    }
}
