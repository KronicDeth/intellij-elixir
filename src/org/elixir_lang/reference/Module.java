package org.elixir_lang.reference;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.indexing.FileBasedIndex;
import org.elixir_lang.ElixirFileType;
import org.elixir_lang.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
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

        results.addAll(multiResolveUpFromElement(myElement));

        if (results.isEmpty()) {
            results.addAll(multiResolveProject(myElement.getProject(), myElement.getContainingFile()));
        }

        return results.toArray(new ResolveResult[results.size()]);
    }

    private Collection<ResolveResult> multiResolveProject(Project project, PsiFile exceptFile) {
        List<ResolveResult> results = new ArrayList<ResolveResult>();
        Collection<VirtualFile> virtualFileCollection = FileBasedIndex.getInstance().getContainingFiles(
                FileTypeIndex.NAME,
                ElixirFileType.INSTANCE,
                GlobalSearchScope.allScope(project)
        );
        PsiManager psiManager = PsiManager.getInstance(project);
        org.elixir_lang.scope_processor.Module moduleScopeProcessor = new org.elixir_lang.scope_processor.Module(myElement);

        for (VirtualFile virtualFile : virtualFileCollection) {
            PsiFile psiFile = psiManager.findFile(virtualFile);

            if (psiFile != null) {
                if (!psiFile.processDeclarations(
                        moduleScopeProcessor,
                        ResolveState.initial(),
                        psiFile,
                        myElement
                )) {
                    ElixirAlias declaration = moduleScopeProcessor.declaration();

                    assert declaration != null;

                    results.add(new PsiElementResolveResult(declaration));

                    break;
                }
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
