package org.elixir_lang.psi.scope.variable;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.util.PsiTreeUtil;
import org.elixir_lang.psi.scope.Variable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static org.elixir_lang.psi.impl.ElixirPsiImplUtil.ENTRANCE;

public class Variants extends Variable {
    /*
     * Static Methods
     */

    @Nullable
    public static List<LookupElement> lookupElementList(@NotNull PsiElement entrance) {
        Variants variants = new Variants();
        PsiTreeUtil.treeWalkUp(
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
     *
     * Instance Methods
     *
     */

    /*
     * Protected Instance Methods
     */

    /**
     * Decides whether {@code match} matches the criteria being searched for.  All other {@link #execute} methods
     * eventually end here.
     *
     * @return {@code false}, as all variables should be found.  Prefix filtering will be done later by IDEA core.
     */
    @Override
    protected boolean executeOnVariable(@NotNull PsiNamedElement match, @NotNull ResolveState state) {
        String name = match.getName();

        if (name != null) {
            if (lookupElementList == null) {
                lookupElementList = new ArrayList<LookupElement>();
            }

            lookupElementList.add(
                    LookupElementBuilder.createWithSmartPointer(
                            name,
                            match
                    )
            );
        }

        return true;
    }

    /*
     * Private Instance Methods
     */

    private List<LookupElement> getLookupElementList() {
        return lookupElementList;
    }
}
