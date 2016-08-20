package org.elixir_lang.psi.scope;

import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import org.elixir_lang.psi.call.Named;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.elixir_lang.psi.stub.type.call.Stub.isModular;

public abstract class Module implements PsiScopeProcessor {
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
    @Override
    public <T> T getHint(@NotNull Key<T> hintKey) {
        return null;
    }

    @Override
    public void handleEvent(@NotNull Event event, @Nullable Object associated) {
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
    protected abstract boolean executeOnModular(@NotNull final PsiNamedElement match, @NotNull ResolveState state);

    /*
     * Private Instance Methods
     */

    private boolean execute(@NotNull Named match, @NotNull ResolveState state) {
        boolean keepProcessing = true;

        if (isModular(match)) {
            keepProcessing = executeOnModular(match, state);
        }

        return keepProcessing;
    }
}
