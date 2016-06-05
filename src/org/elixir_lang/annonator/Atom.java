package org.elixir_lang.annonator;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiRecursiveElementVisitor;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import org.elixir_lang.ElixirSyntaxHighlighter;
import org.elixir_lang.psi.ElixirKeywordKey;
import org.jetbrains.annotations.NotNull;

/**
 * Annotates things that act like Atom as Atom
 */
public class Atom implements Annotator, DumbAware {
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
    @Override
    public void annotate(@NotNull final PsiElement element, @NotNull final AnnotationHolder holder) {
       element.accept(
               new PsiRecursiveElementVisitor() {
                   /*
                    * Public Instance Methods
                    */

                   @Override
                   public void visitElement(PsiElement element) {
                       if (element instanceof ElixirKeywordKey) {
                         visitKeywordKey((ElixirKeywordKey) element);
                       }
                   }

                   /*
                    * Private Instance Methods
                    */

                   private void visitKeywordKey(ElixirKeywordKey keywordKey) {
                       PsiElement child = keywordKey.getFirstChild();

                       // a normal, non-quoted keyword key
                       if (child instanceof LeafPsiElement) {
                           TextRange keywordKeyTextRange = keywordKey.getTextRange();
                           // highlight the `:` as part of the pseudo-atom
                           TextRange atomTextRange = new TextRange(
                                   keywordKeyTextRange.getStartOffset(),
                                   keywordKeyTextRange.getEndOffset() + 1
                           );
                           highlight(atomTextRange, holder, ElixirSyntaxHighlighter.ATOM);
                       }
                   }
               }
       );
    }

    /*
     * Private Instance Methods
     */

    /**
     * Highlights `textRange` with the given `textAttributesKey`.
     *
     * @param textRange         textRange in the document to highlight
     * @param annotationHolder  the container which receives annotations created by the plugin.
     * @param textAttributesKey text attributes to apply to the `node`.
     */
    private void highlight(@NotNull final TextRange textRange,
                           @NotNull AnnotationHolder annotationHolder,
                           @NotNull final TextAttributesKey textAttributesKey) {
        annotationHolder.createInfoAnnotation(textRange, null)
                .setEnforcedTextAttributes(TextAttributes.ERASE_MARKER);
        annotationHolder.createInfoAnnotation(textRange, null)
                .setEnforcedTextAttributes(
                        EditorColorsManager.getInstance().getGlobalScheme().getAttributes(textAttributesKey)
                );
    }
}
