package org.elixir_lang.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.openapi.project.DumbAware
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiRecursiveElementVisitor
import org.elixir_lang.ElixirSyntaxHighlighter
import org.elixir_lang.psi.QualifiableAlias
import org.elixir_lang.psi.impl.isOutermostQualifiableAlias

/**
 * Annotates aliases
 */
class Alias : Annotator, DumbAware {
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
                        if (element is QualifiableAlias) {
                            visitQualifiableAlias(element)
                        }
                    }

                    /*
                    * Private Instance Methods
                    */
                    private fun visitQualifiableAlias(qualifiableAlias: QualifiableAlias) {
                        if (qualifiableAlias.isOutermostQualifiableAlias()) {
                            Highlighter.highlight(holder, ElixirSyntaxHighlighter.ALIAS)
                        }
                    }
                }
        )
    }
}
