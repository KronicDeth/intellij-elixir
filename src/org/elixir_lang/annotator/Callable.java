package org.elixir_lang.annotator;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.util.containers.ContainerUtil;
import org.elixir_lang.ElixirSyntaxHighlighter;
import org.elixir_lang.errorreport.Logger;
import org.elixir_lang.psi.AtNonNumericOperation;
import org.elixir_lang.psi.AtUnqualifiedNoParenthesesCall;
import org.elixir_lang.psi.call.Call;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static org.elixir_lang.psi.call.name.Module.KERNEL;
import static org.elixir_lang.psi.call.name.Module.KERNEL_SPECIAL_FORMS;
import static org.elixir_lang.reference.Callable.BIT_STRING_TYPES;
import static org.elixir_lang.reference.Callable.isBitStreamSegmentOption;

/**
 * Annotates callables.
 */
public class Callable implements Annotator, DumbAware {
    /*
     * CONSTANTS
     */

    private static final TextAttributesKey[] FUNCTION_CALL_TEXT_ATTRIBUTE_KEYS = {
            ElixirSyntaxHighlighter.FUNCTION_CALL
    };
    private static final TextAttributesKey[] IGNORED_VARIABLE_TEXT_ATTRIBUTE_KEYS = {
            ElixirSyntaxHighlighter.IGNORED_VARIABLE
    };
    private static final TextAttributesKey[] MACRO_CALL_TEXT_ATTRIBUTES_KEYS = {ElixirSyntaxHighlighter.MACRO_CALL};
    private static final TextAttributesKey[] PARAMETER_TEXT_ATTRIBUTE_KEYS = {ElixirSyntaxHighlighter.PARAMETER};
    private static final TextAttributesKey[] PREDEFINED_FUNCTION_CALL_TEXT_ATTRIBUTE_KEYS = {
            ElixirSyntaxHighlighter.FUNCTION_CALL,
            ElixirSyntaxHighlighter.PREDEFINED_CALL
    };

    private static final Set<String> PREDEFINED_LOCATION_STRING_SET = new HashSet<>(
            Arrays.asList(
                    KERNEL,
                    KERNEL_SPECIAL_FORMS
            )
    );
    private static final TextAttributesKey[] PREDEFINED_MACRO_CALL_TEXT_ATTRIBUTES_KEYS = {
            ElixirSyntaxHighlighter.MACRO_CALL,
            ElixirSyntaxHighlighter.PREDEFINED_CALL
    };
    private static final TextAttributesKey[] VARIABLE_TEXT_ATTRIBUTE_KEYS = {ElixirSyntaxHighlighter.VARIABLE};

    private static TextAttributesKey[] referrerTextAttributesKeys(
            @NotNull Parameter parameter,
            @NotNull TextAttributesKey[] standardTextAttributeKeys,
            @NotNull TextAttributesKey[] predefinedTextAttributesKeys
    ) {
        TextAttributesKey[] textAttributesKeys = standardTextAttributeKeys;
        PsiElement entrance = parameter.entrance;

        if (parameter.entrance instanceof NavigationItem) {
            NavigationItem navigationItem = (NavigationItem) entrance;
            ItemPresentation presentation = navigationItem.getPresentation();

            if (presentation != null && PREDEFINED_LOCATION_STRING_SET.contains(presentation.getLocationString())) {
                textAttributesKeys = predefinedTextAttributesKeys;
            }
        }

        return textAttributesKeys;
    }

    private static boolean sameFile(@NotNull PsiElement referrer, @NotNull PsiElement resolved) {
        @NotNull PsiFile referrerPsiFile = referrer.getContainingFile();
        VirtualFile referrerVirtualFile = referrerPsiFile.getVirtualFile();
        @NotNull PsiFile resolvedPsiFile = resolved.getContainingFile();
        VirtualFile resolvedVirtualFile = resolvedPsiFile.getVirtualFile();

        return Comparing.equal(referrerVirtualFile, resolvedVirtualFile);
    }

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
                new PsiElementVisitor() {
                    /*
                     * Public Instance Methods
                     */

                    private void visitCall(@NotNull final Call call) {
                        if (!(call instanceof AtNonNumericOperation ||
                                call instanceof AtUnqualifiedNoParenthesesCall)) {
                            visitNonModuleAttributeCall(call);
                        }
                    }

                    /*
                     * Private Instance Methods
                     */

                    private void visitCallDefinitionClause(@NotNull final Call call) {
                        // visit the `def(macro)?p? for Kernel PREDEFINED highlighting
                        visitPlainCall(call);

                        PsiElement head = org.elixir_lang.structure_view.element.CallDefinitionClause.head(call);

                        if (head != null) {
                            visitCallDefinitionHead(head, call);
                        }
                    }

                    /*
                     * Private Instance Methods
                     */

                    private void visitCallDefinitionHead(@NotNull final PsiElement head, @NotNull final Call clause) {
                        PsiElement stripped = org.elixir_lang.structure_view.element.CallDefinitionHead.strip(head);

                        if (stripped instanceof Call) {
                            visitStrippedCallDefinitionHead((Call) stripped, clause);
                        }
                    }

                    @Override
                    public void visitElement(@NotNull final PsiElement element) {
                        if (element instanceof Call) {
                            visitCall((Call) element);
                        }
                    }

                    private void visitNonModuleAttributeCall(@NotNull final Call call) {
                        if (org.elixir_lang.structure_view.element.CallDefinitionClause.is(call)) {
                            visitCallDefinitionClause(call);
                        } else {
                            visitPlainCall(call);
                        }
                    }

                    private void visitPlainCall(@NotNull Call call) {
                        PsiReference reference = call.getReference();

                        if (reference != null) {
                            Collection<PsiElement> resolvedCollection = null;

                            if (reference instanceof PsiPolyVariantReference) {
                                PsiPolyVariantReference polyVariantReference = (PsiPolyVariantReference) reference;

                                ResolveResult[] resolveResults;

                                try {
                                    resolveResults = polyVariantReference.multiResolve(false);
                                } catch (StackOverflowError stackOverflowError) {
                                    Logger.error(Callable.class, "StackOverflowError when annotating Call", call);
                                    resolveResults = new ResolveResult[0];
                                }

                                List<ResolveResult> validResolveResults = ContainerUtil.filter(
                                        resolveResults,
                                        ResolveResult::isValidResult
                                );
                                resolvedCollection = ContainerUtil.map(
                                        validResolveResults,
                                        ResolveResult::getElement
                                );
                            } else {
                                PsiElement resolved = reference.resolve();

                                if (resolved != null) {
                                    resolvedCollection = Collections.singleton(resolved);
                                }
                            }

                            if (resolvedCollection != null && resolvedCollection.size() > 0) {
                                highlight(call, reference.getRangeInElement(), resolvedCollection, holder);
                            } else if (call.hasDoBlockOrKeyword()) {
                                /* Even though it can't be resolved, it is called like a macro, so highlight like
                                   one */
                                PsiElement functionNameElement = call.functionNameElement();

                                if (functionNameElement != null) {
                                    highlight(
                                            functionNameElement.getTextRange(),
                                            holder,
                                            ElixirSyntaxHighlighter.MACRO_CALL
                                    );
                                }
                            }
                        } else if (isBitStreamSegmentOption(call)) {
                            String name = call.getName();

                            if (name != null && BIT_STRING_TYPES.contains(name)) {
                                highlight(call, holder, ElixirSyntaxHighlighter.TYPE);
                            }
                        }
                    }

                    private void visitStrippedCallDefinitionHead(@NotNull final Call stripped,
                                                                 @NotNull final Call clause) {
                        PsiElement functionNameElement = stripped.functionNameElement();

                        if (functionNameElement != null) {
                            TextAttributesKey textAttributeKey = null;

                            if (org.elixir_lang.structure_view.element.CallDefinitionClause.isFunction(clause)) {
                                textAttributeKey = ElixirSyntaxHighlighter.FUNCTION_DECLARATION;
                            } else if (org.elixir_lang.structure_view.element.CallDefinitionClause.isMacro(clause)) {
                                textAttributeKey = ElixirSyntaxHighlighter.MACRO_DECLARATION;
                            }

                            if (textAttributeKey != null) {
                                highlight(functionNameElement, holder, textAttributeKey);
                            }
                        }
                    }
                }
        );
    }

    @Nullable
    private CallHighlight callHighlight(
            @NotNull PsiElement resolved,
            @Nullable CallHighlight previousCallHighlight
    ) {
        CallHighlight callHighlight = null;

        if (org.elixir_lang.reference.Callable.isIgnored(resolved)) {
            callHighlight = CallHighlight.nullablePut(
                    previousCallHighlight,
                    IGNORED_VARIABLE_TEXT_ATTRIBUTE_KEYS,
                    resolved,
                    ElixirSyntaxHighlighter.IGNORED_VARIABLE
            );
        } else {
            Parameter parameter = Parameter.putParameterized(new Parameter(resolved));
            Parameter.Type parameterType = parameter.type;
            TextAttributesKey[] referrerTextAttributesKeys;

            if (parameterType != null) {
                switch (parameterType) {
                    case FUNCTION_NAME:
                        referrerTextAttributesKeys = referrerTextAttributesKeys(
                                parameter,
                                FUNCTION_CALL_TEXT_ATTRIBUTE_KEYS,
                                PREDEFINED_FUNCTION_CALL_TEXT_ATTRIBUTE_KEYS
                        );

                        callHighlight = CallHighlight.nullablePut(
                                previousCallHighlight,
                                referrerTextAttributesKeys,
                                resolved,
                                // will be handled visitCallDefinitionClause
                                null
                        );

                        break;

                    case MACRO_NAME:
                        referrerTextAttributesKeys = referrerTextAttributesKeys(
                                parameter,
                                MACRO_CALL_TEXT_ATTRIBUTES_KEYS,
                                PREDEFINED_MACRO_CALL_TEXT_ATTRIBUTES_KEYS
                        );

                        callHighlight = CallHighlight.nullablePut(
                                previousCallHighlight,
                                referrerTextAttributesKeys,
                                resolved,
                                // will be handled visitCallDefinitionClause
                                null
                        );

                        break;

                    case VARIABLE:
                        callHighlight = CallHighlight.nullablePut(
                                previousCallHighlight,
                                PARAMETER_TEXT_ATTRIBUTE_KEYS,
                                resolved,
                                ElixirSyntaxHighlighter.PARAMETER
                        );
                }
            } else if (org.elixir_lang.reference.Callable.isParameterWithDefault(resolved)) {
                callHighlight = CallHighlight.nullablePut(
                        previousCallHighlight,
                        PARAMETER_TEXT_ATTRIBUTE_KEYS,
                        resolved,
                        ElixirSyntaxHighlighter.PARAMETER
                );
            } else if (org.elixir_lang.reference.Callable.isVariable(resolved)) {
                callHighlight = CallHighlight.nullablePut(
                        previousCallHighlight,
                        VARIABLE_TEXT_ATTRIBUTE_KEYS,
                        resolved,
                        ElixirSyntaxHighlighter.VARIABLE
                );
            }
        }

        return callHighlight;
    }

    @Nullable
    private CallHighlight callHighlight(@NotNull Collection<PsiElement> resolvedCollection) {
        CallHighlight callHighlight = null;

        for (PsiElement resolved : resolvedCollection) {
            callHighlight =
                    callHighlight(resolved, callHighlight);
        }

        return callHighlight;
    }

    private void highlight(@NotNull Call referrer,
                           @NotNull TextRange rangeInReferrer,
                           @NotNull Collection<PsiElement> resolvedCollection,
                           @NotNull AnnotationHolder annotationHolder) {
        CallHighlight callHighlight = callHighlight(resolvedCollection);

        if (callHighlight != null) {
            TextAttributesKey[] referrerTextAttributesKeys = callHighlight.referrerTextAttributeKeys;

            if (referrerTextAttributesKeys != null) {
                highlight(referrer, rangeInReferrer, annotationHolder, referrerTextAttributesKeys);
            }

            TextAttributesKey resolvedTextAttributesKey = callHighlight.resolvedTextAttributeKey;

            /* Annotations can only be applied to the single, active file, which belongs to the referrer.  The resolved
               may be outside the file if it is a cross-file function or macro usage */
            if (resolvedTextAttributesKey != null) {
                PsiElement resolved = callHighlight.resolved;

                if (resolved != null && sameFile(referrer, resolved)) {
                    highlight(resolved, annotationHolder, resolvedTextAttributesKey);
                }
            }
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
                           @NotNull final TextAttributesKey... textAttributesKeys) {
        TextRange elementRangeInDocument = element.getTextRange();
        int startOffset = elementRangeInDocument.getStartOffset();

        TextRange rangeInElementInDocument = new TextRange(
                startOffset + rangeInElement.getStartOffset(),
                startOffset + rangeInElement.getEndOffset()
        );

        highlight(rangeInElementInDocument, annotationHolder, textAttributesKeys);
    }

    /**
     * Highlights `textRange` with the given `textAttributesKey`.
     *
     * @param textRange          textRange in the document to highlight
     * @param annotationHolder   the container which receives annotations created by the plugin.
     * @param textAttributesKeys text attributes to apply to the `node`.
     */
    private void highlight(@NotNull final TextRange textRange,
                           @NotNull AnnotationHolder annotationHolder,
                           @NotNull final TextAttributesKey... textAttributesKeys) {
        if (textAttributesKeys.length > 0) {
            annotationHolder
                    .createInfoAnnotation(textRange, null)
                    .setEnforcedTextAttributes(TextAttributes.ERASE_MARKER);

            EditorColorsScheme editorColorsScheme = EditorColorsManager
                    .getInstance()
                    .getGlobalScheme();
            TextAttributes mergedTextAttributes = null;

            for (TextAttributesKey textAttributesKey : textAttributesKeys) {
                TextAttributes textAttributes = editorColorsScheme.getAttributes(textAttributesKey);

                if (mergedTextAttributes == null) {
                    mergedTextAttributes = textAttributes;
                } else {
                    mergedTextAttributes = TextAttributes.merge(mergedTextAttributes, textAttributes);
                }
            }

            annotationHolder
                    .createInfoAnnotation(textRange, null)
                    .setEnforcedTextAttributes(mergedTextAttributes);
        }
    }

    private static class CallHighlight {
        @Nullable
        public final PsiElement resolved;
        @Nullable
        final TextAttributesKey[] referrerTextAttributeKeys;
        @Nullable
        final TextAttributesKey resolvedTextAttributeKey;

        private CallHighlight(@Nullable TextAttributesKey[] referrerTextAttributeKeys,
                              @Nullable PsiElement resolved,
                              @Nullable TextAttributesKey resolvedTextAttributeKey) {
            this.referrerTextAttributeKeys = referrerTextAttributeKeys;
            this.resolved = resolved;
            this.resolvedTextAttributeKey = resolvedTextAttributeKey;
        }

        static CallHighlight nullablePut(
                @Nullable CallHighlight callHighlight,
                @Nullable TextAttributesKey[] referrerTextAttributeKeys,
                @Nullable PsiElement resolved,
                @Nullable TextAttributesKey resolvedTextAttributeKey
        ) {
            CallHighlight updatedCallHighlight;

            if (callHighlight == null) {
                updatedCallHighlight = new CallHighlight(
                        referrerTextAttributeKeys,
                        resolved,
                        resolvedTextAttributeKey
                );
            } else {
                updatedCallHighlight = callHighlight.put(
                        referrerTextAttributeKeys,
                        resolved,
                        resolvedTextAttributeKey
                );
            }

            return updatedCallHighlight;
        }

        @Nullable
        TextAttributesKey[] bestReferrerTextAttributeKeys(@Nullable TextAttributesKey[] referrerTextAttributeKeys) {
            TextAttributesKey[] best;

            if (this.referrerTextAttributeKeys != null) {
                if (referrerTextAttributeKeys != null) {
                    boolean currentPredefined = false;

                    for (TextAttributesKey textAttributesKey : this.referrerTextAttributeKeys) {
                        if (textAttributesKey == ElixirSyntaxHighlighter.PREDEFINED_CALL) {
                            currentPredefined = true;
                            break;
                        }
                    }

                    if (currentPredefined) {
                        best = this.referrerTextAttributeKeys;
                    } else {
                        best = referrerTextAttributeKeys;
                    }
                } else {
                    best = this.referrerTextAttributeKeys;
                }
            } else {
                best = referrerTextAttributeKeys;
            }

            return best;
        }

        @NotNull
        public CallHighlight put(@Nullable TextAttributesKey[] referrerTextAttributeKeys,
                                 @Nullable PsiElement resolved,
                                 @Nullable TextAttributesKey resolvedTextAttributeKey) {
            TextAttributesKey[] updatedReferrerTextAttributeKeys =
                    bestReferrerTextAttributeKeys(referrerTextAttributeKeys);

            CallHighlight updatedCallHighlight;

            if (updatedReferrerTextAttributeKeys == this.referrerTextAttributeKeys) {
                updatedCallHighlight = this;
            } else {
                updatedCallHighlight = new CallHighlight(
                        updatedReferrerTextAttributeKeys,
                        resolved,
                        resolvedTextAttributeKey
                );
            }

            return updatedCallHighlight;
        }
    }
}
