package org.elixir_lang.annonator;

import com.intellij.lang.ASTNode;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.project.DumbAware;
import com.intellij.psi.PsiElement;
import org.elixir_lang.ElixirSyntaxHighlighter;
import org.elixir_lang.psi.ElixirUnmatchedUnqualifiedNoParenthesesCall;
import org.elixir_lang.psi.ElixirVisitor;
import org.jetbrains.annotations.NotNull;

/**
 * Annotates functions and macros from `Kernel` module.
 */
public class Kernel implements Annotator, DumbAware {
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
        element.accept(new ElixirVisitor() {
           @Override
           public void visitUnmatchedUnqualifiedNoParenthesesCall(@NotNull ElixirUnmatchedUnqualifiedNoParenthesesCall unmatchedUnqualifiedNoParenthesesCall) {
               // the Kernel annotator only cares about Elixir.Kernel
               if (unmatchedUnqualifiedNoParenthesesCall.resolvedModuleName().equals("Elixir.Kernel")) {
                   String resolvedFunctionName = unmatchedUnqualifiedNoParenthesesCall.resolvedFunctionName();

                   if (resolvedFunctionName.equals("defmodule")) {
                       ASTNode node = unmatchedUnqualifiedNoParenthesesCall.getNode();
                       ASTNode identifier = node.getFirstChildNode();
                       highlight(identifier, holder, ElixirSyntaxHighlighter.KERNEL_MACRO);
                   }
               }
           }
        });
    }

    /*
     * Private Instance Methods
     */

    /**
     * Highlights `node` with the given `textAttributesKey`.
     *
     * @param node node to highlight
     * @param annotationHolder the container which receives annotations created by the plugin.
     * @param textAttributesKey text attributes to apply to the `textRange`.
     */
    private void highlight(@NotNull ASTNode node, @NotNull AnnotationHolder annotationHolder, @NotNull TextAttributesKey textAttributesKey) {
        annotationHolder.createInfoAnnotation(node, null).setEnforcedTextAttributes(TextAttributes.ERASE_MARKER);
        annotationHolder.createInfoAnnotation(node, null).setEnforcedTextAttributes(EditorColorsManager.getInstance().getGlobalScheme().getAttributes(textAttributesKey));
    }
}
