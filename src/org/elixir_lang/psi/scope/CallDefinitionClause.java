package org.elixir_lang.psi.scope;

import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import org.elixir_lang.psi.call.Call;
import org.elixir_lang.structure_view.element.modular.Module;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.elixir_lang.psi.impl.ElixirPsiImplUtil.macroChildCalls;

public abstract class CallDefinitionClause implements PsiScopeProcessor {
    /*
     * Public Instance Methods
     */

    /**
     * @param element candidate element.
     * @param state   current state of resolver.
     * @return false to stop processing.
     */
    @Override
    public boolean execute(@NotNull PsiElement element, @NotNull ResolveState state) {
        boolean keepProcessing = true;

        if (element instanceof Call) {
            keepProcessing = execute((Call) element, state);
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
     * Called on every {@link Call} where {@link org.elixir_lang.structure_view.element.CallDefinitionClause#is} is
     * {@code true} when checking tree with {@link #execute(Call, ResolveState)}
     *
     * @return {@code true} to keep searching up tree; {@code false} to stop searching.
     */
    protected abstract boolean executeOnCallDefinitionClause(Call element, ResolveState state);

    /**
     * Whether to continue searching after each Module's children have been searched.
     * @return {@code true} to keep searching up the PSI tree; {@code false} to stop searching.
     */
    protected abstract boolean keepProcessing();

    /*
     * Private Instance Methods
     */

    private boolean execute(@NotNull Call element, @NotNull ResolveState state) {
        boolean keepProcessing = true;

        if (org.elixir_lang.structure_view.element.CallDefinitionClause.is(element)) {
            keepProcessing = executeOnCallDefinitionClause(element, state);
        } else if (Module.is(element)) {
            Call[] childCalls = macroChildCalls(element);

            if (childCalls != null) {
                for (Call childCall : childCalls) {
                    if(!execute(childCall, state)) {
                        break;
                    }
                }
            }

            // Only check MultiResolve.keepProcessing at the end of a Module to all multiple arities
            keepProcessing = keepProcessing();
        }

        return keepProcessing;
    }

}
