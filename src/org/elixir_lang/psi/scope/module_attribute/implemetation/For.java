package org.elixir_lang.psi.scope.module_attribute.implemetation;

import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.util.PsiTreeUtil;
import org.elixir_lang.psi.call.Call;
import org.elixir_lang.structure_view.element.modular.Module;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

import static org.elixir_lang.psi.call.name.Function.DEFMODULE;
import static org.elixir_lang.psi.call.name.Module.KERNEL;
import static org.elixir_lang.psi.impl.ElixirPsiImplUtil.ENTRANCE;
import static org.elixir_lang.structure_view.element.CallDefinitionClause.enclosingModularMacroCall;

public class For implements PsiScopeProcessor {
    /*
     *
     * Static Methods
     *
     */

    @Nullable
    public static List<ResolveResult> resolveResultList(boolean validResult, @NotNull PsiElement entrance) {
        For scopeProcessor = new For(validResult);
        PsiTreeUtil.treeWalkUp(
                scopeProcessor,
                entrance,
                entrance.getContainingFile(),
                ResolveState.initial().put(ENTRANCE, entrance)
        );

        return scopeProcessor.getResolveResultList();
    }

    /*
     * Fields
     */

    private final boolean validResult;
    @Nullable
    private List<ResolveResult> resolveResultList = null;

    /*
     * Constructors
     */

    public For(boolean validResult) {
        this.validResult = validResult;
    }

    /*
     *
     * Instance Methods
     *
     */

    /*
     * Public Instance Methods
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

    @Nullable
    public List<ResolveResult> getResolveResultList() {
        return resolveResultList;
    }

    @Override
    public void handleEvent(@NotNull Event event, @Nullable Object associated) {

    }

    /*
     * Private Instance Methods
     */

    private boolean execute(@NotNull Call call, @NotNull @SuppressWarnings("unused") ResolveState state) {
        boolean keepProcessing = true;

        if (org.elixir_lang.structure_view.element.modular.Implementation.is(call)) {
            PsiElement element = org.elixir_lang.structure_view.element.modular.Implementation.forNameElement(call);
            boolean validResult;

            if (element != null) {
                validResult = this.validResult;
            } else {
                Call enclosingModularMacroCall = enclosingModularMacroCall(call);

                if (enclosingModularMacroCall != null) {
                    if (enclosingModularMacroCall.isCalling(KERNEL, DEFMODULE)) {
                        element = Module.nameIdentifier(enclosingModularMacroCall);
                        validResult = this.validResult;
                    } else {
                        element = enclosingModularMacroCall;
                        validResult = false;
                    }
                } else {
                    element = call;
                    validResult = false;
                }
            }

            resolveResultList = Collections.<ResolveResult>singletonList(new PsiElementResolveResult(element, validResult));

            keepProcessing = false;
        }

        return keepProcessing;
    }
}
