package org.elixir_lang.psi.scope.module;

import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import org.elixir_lang.psi.scope.Module;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

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
     * @param match
     * @param state
     * @return {@code true} to keep processing; {@code false} to stop processing.
     */
    @Override
    protected boolean executeOnModular(@NotNull PsiNamedElement match, @NotNull ResolveState state) {
        String matchName = match.getName();

        if (matchName != null) {
            Boolean validResult = null;

            if (matchName.equals(name)) {
                validResult = true;
            } else if (incompleteCode && matchName.startsWith(name)) {
                validResult = false;
            }

            if (validResult != null) {
                addToResolveResultList(match, validResult);
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
}
