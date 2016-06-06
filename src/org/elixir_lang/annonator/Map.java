package org.elixir_lang.annonator;

import com.intellij.lang.ASTNode;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiRecursiveElementVisitor;
import org.elixir_lang.ElixirSyntaxHighlighter;
import org.elixir_lang.psi.ElixirMapArguments;
import org.elixir_lang.psi.ElixirMapOperation;
import org.elixir_lang.psi.ElixirStructOperation;
import org.jetbrains.annotations.NotNull;

import static org.elixir_lang.ElixirSyntaxHighlighter.BRACES_TOKEN_SET;


/**
 * Annotates maps and structs
 */
public class Map implements Annotator, DumbAware {
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
                       if (element instanceof ElixirMapOperation) {
                         visitMapOperation((ElixirMapOperation) element);
                       } else if (element instanceof ElixirStructOperation) {
                           visitStructOperation((ElixirStructOperation) element);
                       }
                   }

                   /*
                    * Private Instance Methods
                    */

                   private void visitMapOperation(ElixirMapOperation mapOperation) {
                       highlight(mapOperation.getMapPrefixOperator(), holder, ElixirSyntaxHighlighter.MAP);
                       highlight(mapOperation.getMapArguments(), holder, ElixirSyntaxHighlighter.MAP);
                   }

                   private void visitStructOperation(ElixirStructOperation structOperation) {
                       highlight(structOperation.getMapPrefixOperator(), holder, ElixirSyntaxHighlighter.STRUCT);
                       /* DO NOT highlight mapExpression.  It will be highlighted as either an alias or module
                          attribute, which are more specific and useful. */
                       highlight(structOperation.getMapArguments(), holder, ElixirSyntaxHighlighter.STRUCT);
                   }
               }
       );
    }

    /*
     * Private Instance Methods
     */

    private void highlight(@NotNull final ElixirMapArguments mapArguments,
                           @NotNull AnnotationHolder annotationHolder,
                           @NotNull final TextAttributesKey textAttributesKey) {
        ASTNode[] braces = mapArguments.getNode().getChildren(BRACES_TOKEN_SET);

        for (ASTNode brace : braces) {
            highlight(brace.getTextRange(), annotationHolder, textAttributesKey);
        }
    }

    private void highlight(@NotNull final PsiElement element,
                           @NotNull AnnotationHolder annotationHolder,
                           @NotNull final TextAttributesKey textAttributesKey) {
        highlight(element.getTextRange(), annotationHolder, textAttributesKey);
    }

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
