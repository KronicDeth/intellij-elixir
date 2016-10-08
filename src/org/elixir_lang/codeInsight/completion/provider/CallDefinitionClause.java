package org.elixir_lang.codeInsight.completion.provider;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveResult;
import com.intellij.util.ProcessingContext;
import org.apache.commons.lang.math.IntRange;
import org.elixir_lang.psi.ElixirAccessExpression;
import org.elixir_lang.psi.ElixirDotInfixOperator;
import org.elixir_lang.psi.QualifiableAlias;
import org.elixir_lang.psi.call.Call;
import org.elixir_lang.psi.stub.type.call.Stub;
import org.elixir_lang.reference.Module;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.elixir_lang.psi.impl.ElixirPsiImplUtil.macroChildCalls;
import static org.elixir_lang.psi.operation.Normalized.operatorIndex;
import static org.elixir_lang.psi.operation.infix.Normalized.leftOperand;
import static org.elixir_lang.structure_view.element.CallDefinitionClause.nameArityRange;

public class CallDefinitionClause extends CompletionProvider<CompletionParameters> {
    /*
     * Private Instance Methods
     */

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
                                    org.elixir_lang.codeInsight.lookup.element.CallDefinitionClause.createWithSmartPointer(
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

    @NotNull
    private static Iterable<LookupElement> callDefinitionClauseLookupElements(@NotNull PsiElement scope) {
        Iterable<LookupElement> lookupElementIterable;

        if (scope instanceof Call) {
            lookupElementIterable = callDefinitionClauseLookupElements((Call) scope);
        } else {
            lookupElementIterable = Collections.emptyList();
        }

        return lookupElementIterable;
    }

    @NotNull
    private static PsiElement resolveFully(@NotNull PsiElement element, @Nullable PsiReference startingReference) {
        PsiElement fullyResolved;
        PsiElement currentResolved = element;
        PsiReference reference = startingReference;

        do {
            if (reference == null) {
                reference = currentResolved.getReference();
            }

            if (reference != null) {
                if (reference instanceof PsiPolyVariantReference) {
                    PsiPolyVariantReference polyVariantReference = (PsiPolyVariantReference) reference;
                    ResolveResult[] resolveResults = polyVariantReference.multiResolve(false);
                    int resolveResultCount = resolveResults.length;

                    if (resolveResultCount == 0) {
                        fullyResolved = currentResolved;

                        break;
                    } else if (resolveResultCount == 1) {
                        ResolveResult resolveResult = resolveResults[0];

                        PsiElement nextResolved = resolveResult.getElement();

                        if (nextResolved == null || nextResolved.isEquivalentTo(currentResolved)) {
                            fullyResolved = currentResolved;
                            break;
                        } else {
                            currentResolved = nextResolved;
                        }
                    } else {
                        PsiElement nextResolved = null;

                        for (ResolveResult resolveResult : resolveResults) {
                            PsiElement resolveResultElement = resolveResult.getElement();

                            if (resolveResultElement != null &&
                                    resolveResultElement instanceof Call &&
                                    Stub.isModular((Call) resolveResultElement)) {
                                nextResolved = resolveResultElement;

                                break;
                            }
                        }

                        if (nextResolved == null || nextResolved.isEquivalentTo(currentResolved)) {
                            fullyResolved = currentResolved;
                            break;
                        } else {
                            currentResolved = nextResolved;
                        }
                    }
                } else {
                    PsiElement nextResolved = reference.resolve();

                    if (nextResolved == null || nextResolved.isEquivalentTo(currentResolved)) {
                        fullyResolved = currentResolved;
                        break;
                    } else {
                        currentResolved = nextResolved;
                    }
                }
            } else {
                fullyResolved = currentResolved;

                break;
            }

            reference = null;
        } while (true);

        return fullyResolved;
    }

    /*
     * Protected Instance Methods
     */

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters,
                                  ProcessingContext context,
                                  @NotNull CompletionResultSet resultSet) {
        PsiElement originalPosition = parameters.getOriginalPosition();
        PsiElement originalParent = null;

        if (originalPosition != null) {
            originalParent = originalPosition.getParent();

            if (originalParent instanceof ElixirDotInfixOperator) {
                PsiElement grandParent = originalParent.getParent();
                PsiElement[] grandParentChildren = grandParent.getChildren();
                int operatorIndex = operatorIndex(grandParentChildren);
                PsiElement leftOperand = leftOperand(grandParentChildren, operatorIndex);
                PsiElement qualifier = leftOperand;

                if (qualifier instanceof ElixirAccessExpression) {
                    PsiElement[] accessExpressionChildren = leftOperand.getChildren();

                    if (accessExpressionChildren.length > 0) {
                        qualifier = accessExpressionChildren[0];
                    }
                }

                if (qualifier instanceof QualifiableAlias) {
                                        /* need to construct reference directly as qualified aliases don't return a
                                           reference except for the outermost */
                    PsiPolyVariantReference reference = new Module(
                            (QualifiableAlias) qualifier
                    );
                    PsiElement fullyResolved = resolveFully(qualifier, reference);

                    resultSet.withPrefixMatcher("").addAllElements(
                            callDefinitionClauseLookupElements(fullyResolved)
                    );
                }

            }
        }
    }
}
