package org.elixir_lang.reference;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import org.elixir_lang.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Module extends PsiReferenceBase<ElixirAlias> implements PsiPolyVariantReference {
    /*
     * Constructors
     */

    public Module(@NotNull ElixirAlias alias) {
        super(alias, TextRange.create(0, alias.getTextLength()));
    }

    /*
     * Public Instance Methods
     */

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        List<ResolveResult> results = new ArrayList<ResolveResult>();

        ElixirAccessExpression parentAccessExpression = (ElixirAccessExpression) myElement.getParent();

        for (PsiElement sibling = parentAccessExpression.getPrevSibling(); sibling != null; sibling = sibling.getPrevSibling()) {
            if (sibling instanceof ElixirUnmatchedUnqualifiedNoParenthesesCall) {
                ElixirUnmatchedUnqualifiedNoParenthesesCall unmatchedUnqualifiedNoParenthesesCall = (ElixirUnmatchedUnqualifiedNoParenthesesCall) sibling;

                if (unmatchedUnqualifiedNoParenthesesCall.resolvedModuleName().equals("Elixir.Kernel") &&
                        unmatchedUnqualifiedNoParenthesesCall.resolvedFunctionName().equals("defmodule") &&
                        unmatchedUnqualifiedNoParenthesesCall.getDoBlock() != null) {
                    ElixirNoParenthesesOneArgument noParenthesesOneArgument = unmatchedUnqualifiedNoParenthesesCall.getNoParenthesesOneArgument();
                    PsiElement[] children = noParenthesesOneArgument.getChildren();

                    assert children.length == 1;

                    PsiElement child = children[0];

                    if (child instanceof ElixirAccessExpression) {
                        ElixirAccessExpression accessExpression = (ElixirAccessExpression) child;
                        ElixirAlias alias = accessExpression.getAlias();

                        if (alias != null) {
                            if (alias.fullyQualifiedName().equals(myElement.fullyQualifiedName())) {
                                results.add(new PsiElementResolveResult(alias));
                            }
                        }
                    }
                    ElixirMatchedExpression matchedExpression = noParenthesesOneArgument.getMatchedExpression();

                    if (matchedExpression != null) {
                        if (matchedExpression.getText().equals(getValue())) {
                            results.add(new PsiElementResolveResult(matchedExpression));
                        }
                    }
                }
            }
        }

        return results.toArray(new ResolveResult[results.size()]);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        ResolveResult[] resolveResults = multiResolve(false);
        return resolveResults.length == 1 ? resolveResults[0].getElement() : null;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        List<LookupElement> variants = new ArrayList<LookupElement>();
        return variants.toArray();
    }
}
