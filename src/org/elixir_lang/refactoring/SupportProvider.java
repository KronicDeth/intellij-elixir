package org.elixir_lang.refactoring;

import com.intellij.psi.PsiElement;
import org.elixir_lang.psi.AtUnqualifiedNoParenthesesCall;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SupportProvider extends com.intellij.lang.refactoring.RefactoringSupportProvider {
    @Override
    public boolean isMemberInplaceRenameAvailable(@NotNull PsiElement element, @Nullable PsiElement context) {
        return element instanceof AtUnqualifiedNoParenthesesCall;
    }
}
