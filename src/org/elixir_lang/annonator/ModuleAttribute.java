package org.elixir_lang.annonator;

import com.intellij.lang.ASTNode;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.editor.colors.CodeInsightColors;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiRecursiveElementVisitor;
import com.intellij.psi.tree.TokenSet;
import org.elixir_lang.ElixirSyntaxHighlighter;
import org.elixir_lang.psi.*;
import org.elixir_lang.psi.call.Call;
import org.elixir_lang.psi.impl.ElixirPsiImplUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;

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

                        String identifier = identifierNode.getText();

                        if (identifier.equals("callback") || identifier.equals("macrocallback")) {
                            highlight(textRange, holder, ElixirSyntaxHighlighter.MODULE_ATTRIBUTE);

                            highlightCallback(atUnqualifiedNoParenthesesCall, holder);
                        } else if (identifier.equals("doc") ||
                                identifier.equals("moduledoc") ||
                                identifier.equals("typedoc")) {
                            highlight(textRange, holder, ElixirSyntaxHighlighter.DOCUMENTATION_MODULE_ATTRIBUTE);

                            highlightDocumentationText(atUnqualifiedNoParenthesesCall, holder);
                        } else if (identifier.equals("opaque") ||
                                identifier.equals("type") ||
                                identifier.equals("typep")) {
                            highlight(textRange, holder, ElixirSyntaxHighlighter.MODULE_ATTRIBUTE);

                            highlightType(atUnqualifiedNoParenthesesCall, holder);
                        } else if (identifier.equals("spec")) {
                            highlight(textRange, holder, ElixirSyntaxHighlighter.MODULE_ATTRIBUTE);

                            highlightSpecification(atUnqualifiedNoParenthesesCall, holder);
                        } else {
                            highlight(textRange, holder, ElixirSyntaxHighlighter.MODULE_ATTRIBUTE);
                        }
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

    private void highlightCallback(@NotNull final AtUnqualifiedNoParenthesesCall atUnqualifiedNoParenthesesCall,
                                   @NotNull final AnnotationHolder annotationHolder) {
        highlightSpecification(atUnqualifiedNoParenthesesCall, annotationHolder, ElixirSyntaxHighlighter.CALLBACK);
    }

    private void highlightDocumentationText(
            @NotNull final AtUnqualifiedNoParenthesesCall atUnqualifiedNoParenthesesCall,
            @NotNull final AnnotationHolder holder
    ) {
        PsiElement noParenthesesOneArgument = atUnqualifiedNoParenthesesCall.getNoParenthesesOneArgument();
        PsiElement[] grandChildren = noParenthesesOneArgument.getChildren();

        if (grandChildren.length == 1) {
            PsiElement grandChild = grandChildren[0];

            PsiElement[] greatGrandChildren = grandChild.getChildren();

            if (greatGrandChildren.length == 1) {
                PsiElement greatGrandChild = greatGrandChildren[0];

                if (greatGrandChild instanceof ElixirAtomKeyword) {
                    ElixirAtomKeyword atomKeyword = (ElixirAtomKeyword) greatGrandChild;
                    String text = atomKeyword.getText();

                    if (text.equals("false")) {
                        holder.createWeakWarningAnnotation(
                                atomKeyword,
                                "Will make documented invisible to the documentation extraction tools like ExDoc.");
                    }
                } else if (greatGrandChild instanceof Heredoc) {
                    Heredoc heredoc = (Heredoc) greatGrandChild;
                    List<HeredocLine> heredocLineList = heredoc.getHeredocLineList();

                    for (Bodied bodied : heredocLineList) {
                        Body body = bodied.getBody();

                        highlightFragments(
                                heredoc,
                                body,
                                holder,
                                ElixirSyntaxHighlighter.DOCUMENTATION_TEXT
                        );
                    }
                } else if (greatGrandChild instanceof Line) {
                    Line line = (Line) greatGrandChild;
                    Body body = line.getBody();

                    highlightFragments(
                            line,
                            body,
                            holder,
                            ElixirSyntaxHighlighter.DOCUMENTATION_TEXT
                    );
                }
            }
        }
    }

    /**
     * Highlights fragment ASTNodes under `body` that have fragment type from `fragmented.getFragmentType()`.
     *
     * @param fragmented supplies fragment type
     * @param body contains fragments
     * @param annotationHolder the container which receives annotations created by the plugin.
     * @param textAttributesKey text attributes to apply to the fragments.
     */
    private void highlightFragments(@NotNull final Fragmented fragmented,
                                    @NotNull final Body body,
                                    @NotNull AnnotationHolder annotationHolder,
                                    @NotNull final TextAttributesKey textAttributesKey) {
        ASTNode bodyNode = body.getNode();
        ASTNode[] fragmentNodes = bodyNode.getChildren(
                TokenSet.create(fragmented.getFragmentType())
        );

        for (ASTNode fragmentNode : fragmentNodes) {
            highlight(
                    fragmentNode.getTextRange(),
                    annotationHolder,
                    textAttributesKey
            );
        }
    }

    /**
     * Highlights the function call name as a `ElixirSyntaxHighlighter.SPECIFICATION
     *
     * @param atUnqualifiedNoParenthesesCall
     * @param annotationHolder
     */
    private void highlightSpecification(@NotNull final AtUnqualifiedNoParenthesesCall atUnqualifiedNoParenthesesCall,
                                        @NotNull final AnnotationHolder annotationHolder) {
        highlightSpecification(atUnqualifiedNoParenthesesCall, annotationHolder, ElixirSyntaxHighlighter.SPECIFICATION);
    }

    /**
     * Highlight the function call name using the given `textAttributesKey`
     *
     * @param atUnqualifiedNoParenthesesCall
     * @param annotationHolder
     * @param textAttributesKey
     */
    private void highlightSpecification(@NotNull final AtUnqualifiedNoParenthesesCall atUnqualifiedNoParenthesesCall,
                                        @NotNull final AnnotationHolder annotationHolder,
                                        @NotNull final TextAttributesKey textAttributesKey) {
        PsiElement noParenthesesOneArgument = atUnqualifiedNoParenthesesCall.getNoParenthesesOneArgument();
        PsiElement[] grandChildren = noParenthesesOneArgument.getChildren();

        if (grandChildren.length == 1) {
            PsiElement grandChild = grandChildren[0];

            if (grandChild instanceof ElixirMatchedTypeOperation) {
                InfixOperation infixOperation = (InfixOperation) grandChild;
                PsiElement leftOperand = infixOperation.leftOperand();

                if (leftOperand instanceof Call) {
                    Call call = (Call) leftOperand;
                    ASTNode functionNameNode = call.functionNameNode();

                    if (functionNameNode != null) {
                        highlight(functionNameNode.getTextRange(), annotationHolder, textAttributesKey);
                    }
                }
            }
        }
    }


    private void highlightType(AtUnqualifiedNoParenthesesCall atUnqualifiedNoParenthesesCall,
                               AnnotationHolder annotationHolder) {
        PsiElement noParenthesesOneArgument = atUnqualifiedNoParenthesesCall.getNoParenthesesOneArgument();
        PsiElement[] grandChildren = noParenthesesOneArgument.getChildren();

        if (grandChildren.length == 1) {
            PsiElement grandChild = grandChildren[0];

            if (grandChild instanceof ElixirMatchedTypeOperation) {
                InfixOperation infixOperation = (InfixOperation) grandChild;
                PsiElement leftOperand = infixOperation.leftOperand();

                if (leftOperand instanceof Call) {
                    Call call = (Call) leftOperand;
                    ASTNode functionNameNode = call.functionNameNode();

                    if (functionNameNode != null) {
                        highlight(functionNameNode.getTextRange(), annotationHolder, ElixirSyntaxHighlighter.TYPE);
                    }
                }
            }
        }
    }
}
