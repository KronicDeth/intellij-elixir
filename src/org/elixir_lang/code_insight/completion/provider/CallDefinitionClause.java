package org.elixir_lang.code_insight.completion.provider;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import org.apache.commons.lang.math.IntRange;
import org.elixir_lang.psi.ElixirEndOfExpression;
import org.elixir_lang.psi.ElixirTypes;
import org.elixir_lang.psi.call.Call;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.elixir_lang.psi.impl.ElixirPsiImplUtil.macroChildCalls;
import static org.elixir_lang.psi.impl.ElixirPsiImplUtil.maybeModularNameToModular;
import static org.elixir_lang.structure_view.element.CallDefinitionClause.nameArityRange;

public class CallDefinitionClause extends CompletionProvider<CompletionParameters> {
    @NotNull
    private static Iterable<LookupElement> callDefinitionClauseLookupElements(@NotNull Call scope) {
        Call[] childCalls = macroChildCalls(scope);
        List<LookupElement> lookupElementList = null;

        if (childCalls != null && childCalls.length > 0) {
            for (Call childCall : childCalls) {
                if (org.elixir_lang.structure_view.element.CallDefinitionClause.is(childCall)) {
                    Pair<String, IntRange> nameArityRange = nameArityRange(childCall);

                    if (nameArityRange != null) {
                        String name = nameArityRange.first;

                        if (name != null) {
                            if (lookupElementList == null) {
                                lookupElementList = new ArrayList<LookupElement>();
                            }

                            lookupElementList.add(
                                    org.elixir_lang.code_insight.lookup.element.CallDefinitionClause.createWithSmartPointer(
                                            nameArityRange.first,
                                            childCall
                                    )
                            );
                        }
                    }
                }
            }
        }

        if (lookupElementList == null) {
            lookupElementList = Collections.emptyList();
        }

        return lookupElementList;
    }

    @Nullable
    private static PsiElement maybeModularName(@NotNull CompletionParameters parameters) {
        PsiElement originalPosition = parameters.getOriginalPosition();
        PsiElement maybeModularName = null;

        if (originalPosition != null) {
            PsiElement originalParent = originalPosition.getParent();

            if (originalParent != null) {
                PsiElement grandParent = originalParent.getParent();

                if (grandParent instanceof org.elixir_lang.psi.qualification.Qualified) {
                    org.elixir_lang.psi.qualification.Qualified qualifiedGrandParent =
                            (org.elixir_lang.psi.qualification.Qualified) grandParent;
                    maybeModularName = qualifiedGrandParent.qualifier();
                } else if (originalParent instanceof ElixirEndOfExpression) {
                    final int originalParentOffset = originalParent.getTextOffset();

                    if (originalParentOffset > 0) {
                        final PsiElement previousElement =
                                parameters.getOriginalFile().findElementAt(originalParentOffset - 1);

                        if (previousElement != null &&
                                previousElement.getNode().getElementType() == ElixirTypes.DOT_OPERATOR) {
                            maybeModularName = previousElement.getPrevSibling();
                        }
                    }
                }
            }
        }

        return maybeModularName;
    }

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters,
                                  ProcessingContext context,
                                  @NotNull CompletionResultSet resultSet) {
        PsiElement maybeModularName = maybeModularName(parameters);

        if (maybeModularName != null) {
            Call modular = maybeModularNameToModular(maybeModularName, maybeModularName.getContainingFile());

            if (modular != null) {
                if (resultSet.getPrefixMatcher().getPrefix().endsWith(".")) {
                    resultSet = resultSet.withPrefixMatcher("");
                }

                resultSet.addAllElements(
                        callDefinitionClauseLookupElements(modular)
                );
            }
        }
    }
}
