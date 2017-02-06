package org.elixir_lang.reference;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import org.elixir_lang.psi.NamedElement;
import org.elixir_lang.psi.QualifiableAlias;
import org.elixir_lang.psi.scope.module.MultiResolve;
import org.elixir_lang.psi.scope.module.Variants;
import org.elixir_lang.psi.stub.index.AllName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.elixir_lang.reference.module.ResolvableName.resolvableName;

public class Module extends PsiReferenceBase<QualifiableAlias> implements PsiPolyVariantReference {
    @NotNull
    private PsiElement maxScope;

    /*
     * Constructors
     */

    public Module(@NotNull QualifiableAlias qualifiableAlias, @NotNull PsiElement maxScope) {
        super(qualifiableAlias, TextRange.create(0, qualifiableAlias.getTextLength()));
        this.maxScope = maxScope;
    }

    /*
     *
     * Instance Methods
     *
     */

    /*
     * Public Instance Methods
     */

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        List<ResolveResult> resolveResultList = null;
        final String name = resolvableName(myElement);

        if (name != null) {
            resolveResultList = MultiResolve.resolveResultList(name, incompleteCode, myElement, maxScope);

            if (resolveResultList == null || resolveResultList.isEmpty()) {
                resolveResultList = multiResolveProject(
                        myElement.getProject(),
                        name
                );
            }
        }

        ResolveResult[] resolveResults;

        if (resolveResultList == null) {
            resolveResults = new ResolveResult[0];
        } else {
            resolveResults = resolveResultList.toArray(new ResolveResult[resolveResultList.size()]);
        }

        return resolveResults;
    }

    /*
     * Private Instance Methods
     */

    private List<ResolveResult> multiResolveProject(@NotNull Project project,
                                                    @NotNull String name) {
        List<ResolveResult> results = new ArrayList<ResolveResult>();

        Collection<NamedElement> namedElementCollection;

        if (DumbService.isDumb(project)) {
            namedElementCollection = Collections.emptyList();
        } else {
            namedElementCollection = StubIndex.getElements(
                    AllName.KEY,
                    name,
                    project,
                    GlobalSearchScope.allScope(project),
                    NamedElement.class
            );
        }

        for (NamedElement namedElement : namedElementCollection) {
            /* The NamedElement may be a ModuleImpl from a .beam.  Using #getNaviationElement() ensures a source
               (either true source or decompiled) is used, which ensures it is a Call. */
            PsiElement navigationElement = namedElement.getNavigationElement();

            results.add(new PsiElementResolveResult(navigationElement));
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
        List<LookupElement> lookupElementList = Variants.lookupElementList(myElement);

        return lookupElementList.toArray(new Object[lookupElementList.size()]);
    }
}
