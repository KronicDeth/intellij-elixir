package org.elixir_lang.reference;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.util.Function;
import org.elixir_lang.psi.NamedElement;
import org.elixir_lang.psi.QualifiableAlias;
import org.elixir_lang.psi.scope.module.Variants;
import org.elixir_lang.psi.stub.index.AllName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Module extends PsiReferenceBase<QualifiableAlias> implements PsiPolyVariantReference {
    /*
     *
     * Static Methods
     *
     */

    /*
     * Public Static Methods
     */

    @NotNull
    public final PsiElement maxScope;

    /*
     * Private Static Methods
     */

    public Module(@NotNull QualifiableAlias qualifiableAlias, @NotNull PsiElement maxScope) {
        super(qualifiableAlias, TextRange.create(0, qualifiableAlias.getTextLength()));
        this.maxScope = maxScope;
    }

    /**
     * Iterates over each navigation element for the PsiElements with {@code name} in {@code project}.
     *
     * @param project Whose index to search for {@code name}
     * @param name Name to search for in {@code project} StubIndex
     * @param function return {@code false} to stop iteration early
     * @return {@code true} if all calls to {@code function} returned {@code true}
     */
    public static boolean forEachNavigationElement(@NotNull Project project,
                                                   @NotNull String name,
                                                   @NotNull Function<PsiElement, Boolean> function) {
        Collection<NamedElement> namedElementCollection = namedElementCollection(project, name);

        return forEachNavigationElement(namedElementCollection, function);
    }

    /*
     * Fields
     */

    /**
     * Iterates over each navigation element for the PsiElements in {@code psiElementCollection}.
     *
     * @param psiElementCollection Collection of PsiElements that aren't guaranteed to be navigation elements, such as
     *                             the binary elements in {@code .beam} files.
     * @param function Return {@code false} to stop processing and abandon enumeration early
     * @return {@code true} if all calls to {@code function} returned {@code true}
     */
    private static boolean forEachNavigationElement(@NotNull Collection<? extends PsiElement> psiElementCollection,
                                                    @NotNull Function<PsiElement, Boolean> function) {
        boolean keepProcessing = true;

        for (PsiElement psiElement : psiElementCollection) {
            /* The psiElement may be a ModuleImpl from a .beam.  Using #getNaviationElement() ensures a source
               (either true source or decompiled) is used. */
            keepProcessing = function.fun(psiElement.getNavigationElement());

            if (!keepProcessing) {
                break;
            }
        }

        return keepProcessing;
    }

    /*
     * Constructors
     */

    private static Collection<NamedElement> namedElementCollection(@NotNull Project project, @NotNull String name) {
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

        return namedElementCollection;
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
        return ResolveCache
                .getInstance(this.myElement.getProject())
                .resolveWithCaching(
                        this,
                        org.elixir_lang.reference.resolver.Module.INSTANCE,
                        false,
                        incompleteCode
                );
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
