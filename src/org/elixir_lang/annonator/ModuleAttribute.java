package org.elixir_lang.annonator;

import com.intellij.lang.ASTNode;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.colors.impl.DefaultColorsScheme;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiRecursiveElementVisitor;
import org.elixir_lang.ElixirSyntaxHighlighter;
import org.elixir_lang.psi.AtUnqualifiedNoParenthesesCall;
import org.elixir_lang.psi.ElixirUnmatchedAtUnqualifiedNoParenthesesCall;
import org.elixir_lang.psi.Quotable;
import org.elixir_lang.psi.impl.ElixirPsiImplUtil;
import org.jetbrains.annotations.NotNull;

/**
 * Annotates module attributes.
 */
public class ModuleAttribute implements Annotator, DumbAware {
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
                    public void visitAtUnqualifiedNoParenthesesCall(@NotNull final AtUnqualifiedNoParenthesesCall atUnqualifiedNoParenthesesCall) {
                        ASTNode node = atUnqualifiedNoParenthesesCall.getNode();
                        ASTNode[] identifierNodes = node.getChildren(ElixirPsiImplUtil.IDENTIFIER_TOKEN_SET);

                        assert identifierNodes.length == 1;

                        Quotable atPrefixOperator = atUnqualifiedNoParenthesesCall.getAtPrefixOperator();
                        ASTNode identifierNode = identifierNodes[0];

                        TextRange textRange = new TextRange(
                                atPrefixOperator.getTextRange().getStartOffset(),
                                identifierNode.getTextRange().getEndOffset()
                        );

                        highlight(textRange, holder, ElixirSyntaxHighlighter.MODULE_ATTRIBUTE);
                    }

                    @Override
                    public void visitElement(@NotNull final PsiElement element) {
                        if (element instanceof AtUnqualifiedNoParenthesesCall) {
                            visitAtUnqualifiedNoParenthesesCall((AtUnqualifiedNoParenthesesCall) element);
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
     * @param textRange textRange in the document to highlight
     * @param annotationHolder the container which receives annotations created by the plugin.
     * @param textAttributesKey text attributes to apply to the `node`.
     */
    private void highlight(@NotNull final TextRange textRange, @NotNull AnnotationHolder annotationHolder, @NotNull final TextAttributesKey textAttributesKey) {
        annotationHolder.createInfoAnnotation(textRange, null).setEnforcedTextAttributes(TextAttributes.ERASE_MARKER);
        annotationHolder.createInfoAnnotation(textRange, null).setEnforcedTextAttributes(EditorColorsManager.getInstance().getGlobalScheme().getAttributes(textAttributesKey));
    }
}
