package org.elixir_lang.reference.module;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import org.elixir_lang.psi.*;
import org.elixir_lang.psi.call.Call;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.elixir_lang.psi.call.name.Function.ALIAS;
import static org.elixir_lang.psi.call.name.Module.KERNEL;
import static org.elixir_lang.psi.impl.ElixirPsiImplUtil.finalArguments;
import static org.elixir_lang.psi.impl.ElixirPsiImplUtil.hasKeywordKey;

public class UnaliasedName {
    /*
     * Public Static Methods
     */

    @Nullable
    public static String unaliasedName(@NotNull PsiNamedElement namedElement) {
        String unaliasedName;

        if (namedElement instanceof QualifiableAlias) {
            unaliasedName = unaliasedName((QualifiableAlias) namedElement);
        } else {
            unaliasedName = namedElement.getName();
        }

        return unaliasedName;
    }

    /*
     * Private Static Methods
     */

    @Nullable
    private static String down(@NotNull PsiElement element) {
        String unaliasedName = null;

        if (element instanceof QualifiableAlias) {
            PsiNamedElement namedElement = (PsiNamedElement) element;

            unaliasedName = namedElement.getName();
        }

        return unaliasedName;
    }

    @Nullable
    private static String unaliasedName(@NotNull QualifiableAlias qualifiableAlias) {
        return up(qualifiableAlias.getParent(), qualifiableAlias);
    }

    @Nullable
    private static String up(@NotNull Call call, @NotNull QualifiableAlias entrance) {
        String unaliasedName = null;

        if (call.isCalling(KERNEL, ALIAS)) {
            PsiElement[] finalArguments = finalArguments(call);

            if (finalArguments != null && finalArguments.length > 0) {
                PsiElement firstArgument = finalArguments[0];

                unaliasedName = down(firstArgument);
            }
        }

        return unaliasedName;
    }

    @Nullable
    private static String up(@Nullable PsiElement element, @NotNull QualifiableAlias entrance) {
        String unaliasedName = null;

        if (element instanceof Call) {
            unaliasedName = up((Call) element, entrance);
        } else if (element instanceof ElixirAccessExpression ||
                element instanceof QuotableArguments ||
                element instanceof QuotableKeywordList) {
            unaliasedName = up(element.getParent(), entrance);
        } else if (element instanceof ElixirMultipleAliases) {
            unaliasedName = down(entrance);
        } else if (element instanceof QuotableKeywordPair) {
            unaliasedName =up((QuotableKeywordPair) element, entrance);
        }

        return unaliasedName;
    }

    @Nullable
    private static String up(@NotNull QuotableKeywordPair element, @NotNull QualifiableAlias entrance) {
        String unaliasedName = null;

        if (hasKeywordKey(element, "as")) {
            unaliasedName = up(element.getParent(), entrance);
        }

        return  unaliasedName;
    }

}
