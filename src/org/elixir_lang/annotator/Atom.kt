package org.elixir_lang.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiRecursiveElementVisitor
import com.intellij.psi.impl.source.tree.LeafPsiElement
import org.elixir_lang.ElixirSyntaxHighlighter
import org.elixir_lang.annotator.Highlighter.highlight
import org.elixir_lang.psi.QuotableKeywordPair

/**
 * Annotates things that act like Atom as Atom
 */
class Atom : Annotator, DumbAware {
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
                        if (element is QuotableKeywordPair) {
                            visitKeywordPair(element)
                        }
                    }

                    /*
                    * Private Instance Methods
                    */
                    private fun visitKeywordPair(keywordPair: QuotableKeywordPair) {
                        val keywordKey = keywordPair.keywordKey
                        val child = keywordKey.firstChild

                        // a normal, non-quoted keyword key
                        if (child is LeafPsiElement) {
                            val keywordKeyTextRange = keywordKey.textRange
                            // highlight the `:` as part of the pseudo-atom
                            val atomTextRange = TextRange(
                                    keywordKeyTextRange.startOffset,
                                    keywordKeyTextRange.endOffset + 1
                            )
                            highlight(holder, atomTextRange, ElixirSyntaxHighlighter.ATOM)
                        }
                    }
                }
        )
    }
}
