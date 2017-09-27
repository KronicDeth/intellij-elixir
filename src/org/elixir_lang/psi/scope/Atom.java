package org.elixir_lang.psi.scope;

import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Atom implements PsiScopeProcessor {
    /**
     * @param element candidate element.
     * @param state   current state of resolver.
     * @return false to stop processing.
     */
    @Override
    public boolean execute(@NotNull PsiElement element, @NotNull ResolveState state) {
        return false;
    }

    @Nullable
    @Override
    public <T> T getHint(@NotNull Key<T> hintKey) {
        return null;
    }

    @Override
    public void handleEvent(@NotNull Event event, @Nullable Object associated) {
    }
}
