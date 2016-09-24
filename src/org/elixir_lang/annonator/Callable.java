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
import com.intellij.psi.PsiReference;
import org.elixir_lang.ElixirSyntaxHighlighter;
import org.elixir_lang.psi.call.Call;
import org.jetbrains.annotations.NotNull;

import static org.elixir_lang.reference.Callable.BIT_STRING_TYPES;
import static org.elixir_lang.reference.Callable.isBitStreamSegmentOption;

/**
 * Annotates callables.
 */
public class Callable implements Annotator, DumbAware {
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
                    public void visitElement(@NotNull final PsiElement element) {
                        if (element instanceof Call) {
                            visitCall((Call) element);
                        }
                    }

                    /*
                     * Private Instance Methods
                     */

                    private void visitCall(@NotNull final Call call) {
                        PsiReference reference = call.getReference();

                        if (reference != null) {
                            PsiElement resolved = reference.resolve();
                            TextRange rangeInCall = reference.getRangeInElement();

                            if (resolved != null) {
                                highlight(call, rangeInCall, resolved, holder);
                            }
                        } else if (isBitStreamSegmentOption(call)) {
                            String name = call.getName();

                            if (name != null && BIT_STRING_TYPES.contains(name)) {
                                highlight(call, holder, ElixirSyntaxHighlighter.TYPE);
                            }
                        }
                    }
                }
        );
    }

    /*
     * Private Instance Methods
     */

    private void highlight(@NotNull Call referrer,
                           @NotNull TextRange rangeInReferrer,
                           @NotNull PsiElement resolved,
                           @NotNull AnnotationHolder annotationHolder) {
        TextAttributesKey referrerTextAttributesKey = null;
        TextAttributesKey resolvedTextAttributesKey = null;

        if (org.elixir_lang.reference.Callable.isIgnored(resolved)) {
            referrerTextAttributesKey = ElixirSyntaxHighlighter.IGNORED_VARIABLE;
            resolvedTextAttributesKey = ElixirSyntaxHighlighter.IGNORED_VARIABLE;
        } else {
            Parameter.Type parameterType = Parameter.type(resolved);

            if (parameterType != null) {
                switch (parameterType) {
                    case FUNCTION_NAME:
                        referrerTextAttributesKey = ElixirSyntaxHighlighter.FUNCTION_CALL;
                        resolvedTextAttributesKey = ElixirSyntaxHighlighter.FUNCTION_DECLARATION;
                        break;

                    case MACRO_NAME:
                        referrerTextAttributesKey = ElixirSyntaxHighlighter.MACRO_CALL;
                        resolvedTextAttributesKey = ElixirSyntaxHighlighter.MACRO_DECLARATION;
                        break;

                    case VARIABLE:
                        referrerTextAttributesKey = ElixirSyntaxHighlighter.PARAMETER;
                        resolvedTextAttributesKey = ElixirSyntaxHighlighter.PARAMETER;
                }
            } else if (org.elixir_lang.reference.Callable.isParameterWithDefault(resolved)) {
                referrerTextAttributesKey = ElixirSyntaxHighlighter.PARAMETER;
                resolvedTextAttributesKey = ElixirSyntaxHighlighter.PARAMETER;
            } else if (org.elixir_lang.reference.Callable.isVariable(resolved)) {
                referrerTextAttributesKey = ElixirSyntaxHighlighter.VARIABLE;
                resolvedTextAttributesKey = ElixirSyntaxHighlighter.VARIABLE;
            }
        }

        if (referrerTextAttributesKey != null) {
            highlight(referrer, rangeInReferrer, annotationHolder, referrerTextAttributesKey);
        }

        if (resolvedTextAttributesKey != null) {
            highlight(resolved, annotationHolder, resolvedTextAttributesKey);
        }
    }

    private void highlight(@NotNull final PsiElement element,
                           @NotNull AnnotationHolder annotationHolder,
                           @NotNull final TextAttributesKey textAttributesKey) {
        highlight(element.getTextRange(), annotationHolder, textAttributesKey);
    }

    private void highlight(@NotNull final PsiElement element,
                          @NotNull TextRange rangeInElement,
                          @NotNull AnnotationHolder annotationHolder,
                          @NotNull final TextAttributesKey textAttributesKey) {
        TextRange elementRangeInDocument = element.getTextRange();
        int startOffset = elementRangeInDocument.getStartOffset();

        TextRange rangeInElementInDocument = new TextRange(
                startOffset + rangeInElement.getStartOffset(),
                startOffset + rangeInElement.getEndOffset()
        );

        highlight(rangeInElementInDocument, annotationHolder, textAttributesKey);
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
