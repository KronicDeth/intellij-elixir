package org.elixir_lang.psi.scope.module;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.ResolveState;
import org.elixir_lang.psi.scope.Module;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.intellij.psi.util.PsiTreeUtil.treeWalkUp;
import static org.elixir_lang.psi.impl.ElixirPsiImplUtil.ENTRANCE;

public class Variants extends Module {
    /*
     * Public Static Methods
     */

    @Nullable
    public static List<LookupElement> lookupElementList(@NotNull PsiElement entrance) {
        Variants variants = new Variants();
        treeWalkUp(
                variants,
                entrance,
                entrance.getContainingFile(),
                ResolveState.initial().put(ENTRANCE, entrance)
        );

        return variants.getLookupElementList();
    }

    /*
     * Fields
     */

    private List<LookupElement> lookupElementList = null;

    /*
     * Protected Instance Methods
     */

    /**
     * Decides whether {@code match} matches the criteria being searched for.  All other {@link #execute} methods
     * eventually end here.
     *
     * @param match
     * @param aliasedName
     * @param state
     * @return {@code true} to keep processing; {@code false} to stop processing.
     */
    @Override
    protected boolean executeOnAliasedName(@NotNull PsiNamedElement match, @NotNull String aliasedName, @NotNull ResolveState state) {
        if (lookupElementList == null) {
            lookupElementList = new ArrayList<LookupElement>();
        }

        lookupElementList.add(
                LookupElementBuilder.createWithSmartPointer(
                        aliasedName,
                        match
                )
        );

        return true;
    }

    /*
     * Private Instance Methods
     */

    private List<LookupElement> getLookupElementList() {
        return lookupElementList;
    }
}
