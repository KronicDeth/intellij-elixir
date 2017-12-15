package org.elixir_lang.code_insight.line_marker_provider;

import com.intellij.codeHighlighting.Pass;
import com.intellij.codeInsight.daemon.DaemonCodeAnalyzerSettings;
import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.openapi.editor.colors.CodeInsightColors;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.editor.markup.SeparatorPlacement;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import org.apache.commons.lang.math.IntRange;
import org.elixir_lang.psi.AtUnqualifiedNoParenthesesCall;
import org.elixir_lang.psi.ElixirTypes;
import org.elixir_lang.psi.call.Call;
import org.elixir_lang.structure_view.element.CallDefinitionClause;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.elixir_lang.psi.impl.ElixirPsiImplUtil.*;
import static org.elixir_lang.structure_view.element.CallDefinitionClause.nameArityRange;
import static org.elixir_lang.structure_view.element.CallDefinitionSpecification.*;

public class CallDefinition implements LineMarkerProvider {
    /*
     * Fields
     */

    private final DaemonCodeAnalyzerSettings daemonCodeAnalyzerSettings;
    private final EditorColorsManager editorColorsManager;

    /*
     * Constructors
     */

    public CallDefinition(DaemonCodeAnalyzerSettings daemonCodeAnalyzerSettings,
                          EditorColorsManager editorColorsManager) {
        this.daemonCodeAnalyzerSettings = daemonCodeAnalyzerSettings;
        this.editorColorsManager = editorColorsManager;
    }

    /*
     *
     * Instance Methods
     *
     */

    /*
     * Public Instance Methods
     */

    @Override
    public void collectSlowLineMarkers(@NotNull List<PsiElement> elements, @NotNull Collection<LineMarkerInfo> result) {
        assert elements != null;
        // do nothing
    }

    @Nullable
    @Override
    public LineMarkerInfo getLineMarkerInfo(@NotNull PsiElement element) {
        LineMarkerInfo lineMarkerInfo = null;

        if (element instanceof AtUnqualifiedNoParenthesesCall) {
            lineMarkerInfo = getLineMarkerInfo((AtUnqualifiedNoParenthesesCall) element);
        } else if (element instanceof Call) {
            lineMarkerInfo = getLineMarkerInfo((Call) element);
        }

        return lineMarkerInfo;
    }

    /*
     * Private Instance Methods
     */

    @NotNull
    private LineMarkerInfo callDefinitionSeparator(
            @NotNull AtUnqualifiedNoParenthesesCall atUnqualifiedNoParenthesesCall
    ) {
        LeafPsiElement leafPsiElement = (LeafPsiElement) atUnqualifiedNoParenthesesCall
                        .getAtIdentifier()
                        .getNode()
                        .findChildByType(ElixirTypes.IDENTIFIER_TOKEN);

        assert leafPsiElement != null :
                "AtUnqualifiedNoParenthesesCall (" +
                        atUnqualifiedNoParenthesesCall.getText() +
                        ") does not have an Identifier token";

        return callDefinitionSeparator(leafPsiElement);
    }

    @NotNull
    private LineMarkerInfo callDefinitionSeparator(@NotNull PsiElement psiElement) {
        LineMarkerInfo lineMarkerInfo;
        lineMarkerInfo = new LineMarkerInfo<>(
                psiElement,
                psiElement.getTextRange(),
                null,
                Pass.UPDATE_ALL,
                null,
                null,
                GutterIconRenderer.Alignment.RIGHT
        );
        EditorColorsScheme editorColorsScheme = editorColorsManager.getGlobalScheme();
        lineMarkerInfo.separatorColor = editorColorsScheme.getColor(CodeInsightColors.METHOD_SEPARATORS_COLOR);
        lineMarkerInfo.separatorPlacement = SeparatorPlacement.TOP;
        return lineMarkerInfo;
    }

    @Nullable
    private LineMarkerInfo getLineMarkerInfo(@NotNull AtUnqualifiedNoParenthesesCall atUnqualifiedNoParenthesesCall) {
        LineMarkerInfo lineMarkerInfo = null;

        String moduleAttributeName = moduleAttributeName(atUnqualifiedNoParenthesesCall);

        if (moduleAttributeName.equals("@doc")) {
            PsiElement previousExpression = siblingExpression(atUnqualifiedNoParenthesesCall, PREVIOUS_SIBLING);
            boolean firstInGroup = true;

            if (previousExpression instanceof AtUnqualifiedNoParenthesesCall) {
                AtUnqualifiedNoParenthesesCall previousModuleAttribute =
                        (AtUnqualifiedNoParenthesesCall) previousExpression;
                String previousModuleAttributeName = moduleAttributeName(previousModuleAttribute);

                if (previousModuleAttributeName.equals("@spec")) {
                    Pair<String, Integer> moduleAttributeNameArity = moduleAttributeNameArity(previousModuleAttribute);

                    if (moduleAttributeNameArity != null) {
                        Call nextSiblingCallDefinitionClause = siblingCallDefinitionClause(atUnqualifiedNoParenthesesCall, NEXT_SIBLING);

                        if (nextSiblingCallDefinitionClause != null) {
                            Pair<String, IntRange> nameArityRange = nameArityRange(nextSiblingCallDefinitionClause);

                            if (nameArityRange != null) {
                                IntRange arityRange = nameArityRange.second;

                                if (arityRange.containsInteger(moduleAttributeNameArity.second)) {
                                    // the previous spec is part of the group
                                    firstInGroup = false;
                                }
                            }
                        }
                    }
                }
            }

            if (firstInGroup) {
                lineMarkerInfo = callDefinitionSeparator(atUnqualifiedNoParenthesesCall);
            }
        } else if (moduleAttributeName.equals("@spec")) {
            PsiElement previousExpression = siblingExpression(atUnqualifiedNoParenthesesCall, PREVIOUS_SIBLING);
            boolean firstInGroup = true;

            if (previousExpression instanceof AtUnqualifiedNoParenthesesCall) {
                AtUnqualifiedNoParenthesesCall previousModuleAttribute =
                        (AtUnqualifiedNoParenthesesCall) previousExpression;
                String previousModuleAttributeName = moduleAttributeName(previousModuleAttribute);

                if (previousModuleAttributeName.equals("@doc")) {
                    firstInGroup = false;
                } else if (previousModuleAttributeName.equals("@spec")) {
                    Pair<String, Integer> moduleAttributeNameArity =
                            moduleAttributeNameArity(atUnqualifiedNoParenthesesCall);

                    if (moduleAttributeNameArity != null) {
                        Pair<String, Integer> previousModuleAttributeNameArity =
                                moduleAttributeNameArity(previousModuleAttribute);

                        if (previousModuleAttributeNameArity != null) {
                            // name match, now check if the arities match.
                            if (moduleAttributeNameArity.first.equals(previousModuleAttributeNameArity.first)) {
                                Integer moduleAttributeArity = moduleAttributeNameArity.second;
                                Integer previousModuleAttributeArity = previousModuleAttributeNameArity.second;

                                if (moduleAttributeArity.equals(previousModuleAttributeArity)) {
                                    /* same arity with different pattern is same function, so the previous @spec should
                                       check if it is first because this one isn't */
                                    firstInGroup = false;
                                } else {
                                    /* same name, but different arity needs to determine if the call definition has an
                                       arity range. */
                                    Call specification = specification(atUnqualifiedNoParenthesesCall);

                                    if (specification != null) {
                                        Call type = specificationType(specification);

                                        if (type != null) {
                                            PsiReference reference = type.getReference();

                                            if (reference != null) {
                                                List<PsiElement> resolvedList = null;

                                                if (reference instanceof PsiPolyVariantReference) {
                                                    PsiPolyVariantReference polyVariantReference =
                                                            (PsiPolyVariantReference) reference;

                                                    ResolveResult[] resolveResults =
                                                            polyVariantReference.multiResolve(false);


                                                    if (resolveResults.length > 0) {
                                                        resolvedList = new ArrayList<PsiElement>();

                                                        for (ResolveResult resolveResult : resolveResults) {
                                                            resolvedList.add(resolveResult.getElement());
                                                        }
                                                    }
                                                } else {
                                                    PsiElement resolved = reference.resolve();

                                                    if (resolved != null) {
                                                        resolvedList = Collections.singletonList(resolved);
                                                    }
                                                }

                                                if (resolvedList != null && resolvedList.size() > 0) {
                                                    for (PsiElement resolved : resolvedList) {
                                                        if (resolved instanceof Call) {
                                                            Pair<String, IntRange> resolvedNameArityRange =
                                                                    nameArityRange((Call) resolved);

                                                            if (resolvedNameArityRange != null) {
                                                                IntRange resolvedArityRange = resolvedNameArityRange.second;

                                                                if (resolvedArityRange.containsInteger(moduleAttributeArity) &&
                                                                        resolvedArityRange.containsInteger(previousModuleAttributeArity)) {
                                                                    // the current @spec and the previous @spec apply to the same call definition clause
                                                                    firstInGroup = false;

                                                                    break;
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                Call specification = specification(atUnqualifiedNoParenthesesCall);

                if (specification != null) {
                    Call type = specificationType(specification);

                    if (type != null) {
                        PsiReference reference = type.getReference();

                        if (reference != null) {
                            if (reference instanceof PsiPolyVariantReference) {
                                PsiPolyVariantReference polyVariantReference = (PsiPolyVariantReference) reference;

                                ResolveResult[] resolveResults = polyVariantReference.multiResolve(false);
                                PsiFile containingFile = type.getContainingFile();

                                for (ResolveResult resolveResult : resolveResults) {
                                    PsiElement element = resolveResult.getElement();

                                    if (element != null) {
                                        if (element.getContainingFile().equals(containingFile) &&
                                                element.getTextOffset() < type.getTextOffset()) {
                                            firstInGroup = false;
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (firstInGroup) {
                lineMarkerInfo = callDefinitionSeparator(atUnqualifiedNoParenthesesCall);
            }
        }

        return lineMarkerInfo;
    }

    @Nullable
    private LineMarkerInfo getLineMarkerInfo(@NotNull Call call) {
        LineMarkerInfo lineMarkerInfo = null;

        if (daemonCodeAnalyzerSettings.SHOW_METHOD_SEPARATORS && CallDefinitionClause.is(call)) {
            Call previousCallDefinitionClause = siblingCallDefinitionClause(call, PREVIOUS_SIBLING);
            boolean firstClause;

            if (previousCallDefinitionClause == null) {
                firstClause = true;
            } else {
                Pair<String, IntRange> callNameArityRange = nameArityRange(call);

                if (callNameArityRange != null) {
                    Pair<String, IntRange> previousNameArityRange = nameArityRange(previousCallDefinitionClause);

                    firstClause = previousNameArityRange == null || !previousNameArityRange.equals(callNameArityRange);
                } else {
                    firstClause = true;
                }
            }

            if (firstClause) {
                PsiElement previousExpression = previousSiblingExpression(call);

                if (previousExpression instanceof AtUnqualifiedNoParenthesesCall) {
                    AtUnqualifiedNoParenthesesCall previousModuleAttributeDefinition =
                            (AtUnqualifiedNoParenthesesCall) previousExpression;
                    String moduleAttributeName = moduleAttributeName(previousModuleAttributeDefinition);

                    if (moduleAttributeName.equals("@doc")) {
                        firstClause = false;
                    } else if (moduleAttributeName.equals("@spec")) {
                        Pair<String, IntRange> callNameArityRange = nameArityRange(call);

                        if (callNameArityRange != null) {
                            Pair<String, Integer> specNameArity =
                                    moduleAttributeNameArity(previousModuleAttributeDefinition);

                            if (specNameArity != null) {
                                Integer specArity = specNameArity.second;

                                IntRange callArityRange = callNameArityRange.second;

                                if (callArityRange.containsInteger(specArity)) {
                                    firstClause = false;
                                }
                            }
                        }
                    }
                }
            }

            if (firstClause) {
                lineMarkerInfo = callDefinitionSeparator(call);
            }
        }

        return lineMarkerInfo;
    }

    @Nullable
    private Call siblingCallDefinitionClause(@NotNull PsiElement element,
                                             @NotNull com.intellij.util.Function<PsiElement, PsiElement> function) {
        PsiElement expression = element;
        Call siblingCallDefinitionClause = null;

        while (expression != null) {
            expression = siblingExpression(expression, function);

            if (expression instanceof Call) {
                Call call = (Call) expression;

                if (CallDefinitionClause.is(call)) {
                    siblingCallDefinitionClause = call;
                    break;
                }
            }
        }

        return siblingCallDefinitionClause;
    }
}
