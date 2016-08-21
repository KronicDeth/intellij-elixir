package org.elixir_lang.psi.scope.module;

import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import org.elixir_lang.psi.scope.Module;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static org.elixir_lang.Module.split;
import static org.elixir_lang.psi.impl.ElixirPsiImplUtil.ENTRANCE;

public class MultiResolve extends Module {
    /*
     * Public Static Methods
     */

    @Nullable
    public static List<ResolveResult> resolveResultList(@NotNull String name,
                                                        boolean incompleteCode,
                                                        @NotNull PsiElement entrance) {
      return resolveResultList(name, incompleteCode, entrance, ResolveState.initial());
    }

    /*
     * Private Static Methods
     */

    @Nullable
    private static List<ResolveResult> resolveResultList(@NotNull String name,
                                                         boolean incompleteCode,
                                                         @NotNull PsiElement entrance,
                                                         @NotNull ResolveState state) {
        MultiResolve multiResolve = new MultiResolve(name, incompleteCode);
        PsiTreeUtil.treeWalkUp(
                multiResolve,
                entrance,
                entrance.getContainingFile(),
                state.put(ENTRANCE, entrance)
        );
        return multiResolve.getResolveResultList();
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
        Boolean validResult = null;

        if (aliasedName.equals(name)) {
            validResult = true;
        } else {
            List<String> namePartList = split(name);
            String firstNamePart = namePartList.get(0);

            // alias Foo.SSH, then SSH.Key is name
            if (aliasedName.equals(firstNamePart)) {
                validResult = true;
            } else if (incompleteCode && aliasedName.startsWith(name)) {
                validResult = false;
            }
        }

        if (validResult != null) {
            addToResolveResultList(match, validResult);
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
}
