package org.elixir_lang.psi.scope.module;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.util.PsiTreeUtil;
import org.elixir_lang.psi.NamedElement;
import org.elixir_lang.psi.call.Named;
import org.elixir_lang.psi.scope.Module;
import org.elixir_lang.psi.stub.index.AllName;
import org.elixir_lang.reference.module.UnaliasedName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.elixir_lang.Module.concat;
import static org.elixir_lang.Module.split;
import static org.elixir_lang.psi.impl.ElixirPsiImplUtil.ENTRANCE;

public class MultiResolve extends Module {
    /*
     * Public Static Methods
     */

    @Nullable
    public static List<ResolveResult> resolveResultList(@NotNull String name,
                                                        boolean incompleteCode,
                                                        @NotNull PsiElement entrance,
                                                        @NotNull PsiElement maxScope) {
      return resolveResultList(name, incompleteCode, entrance, maxScope, ResolveState.initial());
    }

    /*
     * Private Static Methods
     */

    @NotNull
    private static Collection<NamedElement> indexedNamedElements(@NotNull PsiNamedElement match,
                                                                 @NotNull String unaliasedName) {
        Project project = match.getProject();
        return StubIndex.getElements(
                AllName.KEY,
                unaliasedName,
                project,
                GlobalSearchScope.allScope(project),
                NamedElement.class
        );
    }

    @Nullable
    private static List<ResolveResult> resolveResultList(@NotNull String name,
                                                         boolean incompleteCode,
                                                         @NotNull PsiElement entrance,
                                                         @NotNull PsiElement maxScope,
                                                         @NotNull ResolveState state) {
        MultiResolve multiResolve = new MultiResolve(name, incompleteCode);
        PsiTreeUtil.treeWalkUp(
                multiResolve,
                entrance,
                maxScope,
                state.put(ENTRANCE, entrance)
        );
        return multiResolve.getResolveResultList();
    }

    @NotNull
    private static String unaliasedName(@NotNull PsiNamedElement match, @NotNull List<String> namePartList) {
        String matchUnaliasedName = UnaliasedName.unaliasedName(match);

        List<String> unaliasedNamePartList = new ArrayList<String>(namePartList.size());
        unaliasedNamePartList.add(matchUnaliasedName);

        for (int i = 1; i < namePartList.size(); i++) {
            unaliasedNamePartList.add(namePartList.get(i));
        }

        return concat(unaliasedNamePartList);
    }

    /*
     * Fields
     */

    @NotNull
    private final String name;
    private final boolean incompleteCode;
    @Nullable
    private List<ResolveResult> resolveResultList = null;

    /*
     * Constructors
     */

    MultiResolve(@NotNull String name, boolean incompleteCode) {
        this.incompleteCode = incompleteCode;
        this.name = name;
    }

    /*
     * Public Instance Methods
     */

    @Override
    public boolean execute(@NotNull PsiElement match, @NotNull ResolveState state) {
        boolean keepProcessing = true;

        if (match instanceof Named) {
            keepProcessing = execute((Named) match, state);
        }

        return keepProcessing;
    }

    @Nullable
    public List<ResolveResult> getResolveResultList() {
        return resolveResultList;
    }

    /*
     * Protected Instance Methods
     */

    /**
     * Decides whether {@code match} matches the criteria being searched for.  All other {@link #execute} methods
     * eventually end here.
     *
     * @return {@code true} to keep processing; {@code false} to stop processing.
     */
    @Override
    protected boolean executeOnAliasedName(@NotNull PsiNamedElement match,
                                           @NotNull String aliasedName,
                                           @NotNull ResolveState state) {
        if (aliasedName.equals(name)) {
            List<String> namePartList = split(name);

            // adds `Foo.SSH` in `alias Foo.SSH`
            addToResolveResultList(match, true);

            // adds `defmodule Foo.SSH` for `alias Foo.SSH`
            addUnaliasedNamedElementsToResolveResultList(match, namePartList);
        } else {
            List<String> namePartList = split(name);
            String firstNamePart = namePartList.get(0);

            // alias Foo.SSH, then SSH.Key is name
            if (aliasedName.equals(firstNamePart)) {
                addToResolveResultList(match, true);

                addUnaliasedNamedElementsToResolveResultList(match, namePartList);
            } else if (incompleteCode && aliasedName.startsWith(name)) {
                addToResolveResultList(match, false);
            }
        }

        return org.elixir_lang.psi.scope.MultiResolve.keepProcessing(incompleteCode, resolveResultList);
    }

    /*
     * Private Instance Methods
     */

    private void addToResolveResultList(@NotNull PsiElement element, boolean validResult) {
        resolveResultList = org.elixir_lang.psi.scope.MultiResolve.addToResolveResultList(
                resolveResultList, new PsiElementResolveResult(element, validResult)
        );
    }

    private void addUnaliasedNamedElementsToResolveResultList(@NotNull PsiNamedElement match, List<String> namePartList) {
        String unaliasedName = unaliasedName(match, namePartList);
        Collection<NamedElement> namedElementCollection = indexedNamedElements(match, unaliasedName);

        for (PsiElement element : namedElementCollection) {
            addToResolveResultList(element, true);
        }
    }
}
