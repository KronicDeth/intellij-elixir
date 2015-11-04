package org.elixir_lang.reference;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import org.elixir_lang.psi.*;
import org.elixir_lang.psi.stub.index.AllName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Module extends PsiReferenceBase<QualifiableAlias> implements PsiPolyVariantReference {
    /*
     * Constructors
     */

    public Module(@NotNull QualifiableAlias qualifiableAlias) {
        super(qualifiableAlias, TextRange.create(0, qualifiableAlias.getTextLength()));
    }

    /*
     * Public Instance Methods
     */

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        List<ResolveResult> results = new ArrayList<ResolveResult>();

        results.addAll(multiResolveUpFromElement(myElement));

        if (results.isEmpty()) {
            results.addAll(multiResolveProject(myElement.getProject(), myElement.getContainingFile()));
        }

        return results.toArray(new ResolveResult[results.size()]);
    }

    private Collection<ResolveResult> multiResolveProject(Project project, PsiFile exceptFile) {
        List<ResolveResult> results = new ArrayList<ResolveResult>();
        final String fullyQualifiedName = myElement.fullyQualifiedName();

        if (fullyQualifiedName != null) {
            Collection<NamedElement> namedElementCollection = StubIndex.getElements(
                    AllName.KEY,
                    fullyQualifiedName,
                    project,
                    GlobalSearchScope.allScope(project),
                    NamedElement.class
            );

            for (NamedElement namedElement : namedElementCollection) {
                results.add(new PsiElementResolveResult(namedElement));
            }
        }

        return results;
    }

    private List<ResolveResult> multiResolveUpFromElement(@NotNull final PsiElement element) {
        List<ResolveResult> results = new ArrayList<ResolveResult>();;
        PsiElement lastSibling = element;

        while (results.isEmpty() && lastSibling != null) {
            results.addAll(multiResolveSibling(lastSibling));

            lastSibling = lastSibling.getParent();
        }

        return results;
    }

    private List<ResolveResult> multiResolveSibling(@NotNull final PsiElement lastSibling) {
        List<ResolveResult> results = new ArrayList<ResolveResult>();

        for (PsiElement sibling = lastSibling; sibling != null; sibling = sibling.getPrevSibling()) {
            if (sibling instanceof ElixirUnmatchedUnqualifiedNoParenthesesCall) {
                ElixirUnmatchedUnqualifiedNoParenthesesCall unmatchedUnqualifiedNoParenthesesCall = (ElixirUnmatchedUnqualifiedNoParenthesesCall) sibling;

                if (unmatchedUnqualifiedNoParenthesesCall.isDefmodule()) {
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

        return results;
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
