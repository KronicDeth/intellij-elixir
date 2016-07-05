package org.elixir_lang.psi.scope.module_attribute;

import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.util.PsiTreeUtil;
import org.elixir_lang.psi.QualifiableAlias;
import org.elixir_lang.psi.call.Call;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

import static org.elixir_lang.psi.impl.ElixirPsiImplUtil.ENTRANCE;

public class Implementation implements PsiScopeProcessor {
    /*
     *
     * Static Methods
     *
     */

    public static List<ResolveResult> resolveResultList(boolean incompleteCode, @NotNull PsiElement entrance) {
        List<ResolveResult> resolveResultList;

        Implementation implementation = new Implementation(incompleteCode);
        PsiTreeUtil.treeWalkUp(
                implementation,
                entrance,
                entrance.getContainingFile(),
                ResolveState.initial().put(ENTRANCE, entrance)
        );

        return implementation.getResolveResultList();
    }

    /*
     * Fields
     */

    private final boolean incompleteCode;
    @Nullable
    private List<ResolveResult> resolveResultList = null;

    /*
     * Constructors
     */

    public Implementation(boolean incompleteCode) {
        this.incompleteCode = incompleteCode;
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
            QualifiableAlias protocolNameElement = org.elixir_lang.structure_view.element.modular.Implementation.protocolNameElement(call);
            PsiElement element;
            boolean validResult;

            if (protocolNameElement != null) {
                element = protocolNameElement;
                validResult = incompleteCode;
            } else {
                element = call;
                validResult = false;
            }

            resolveResultList = Collections.<ResolveResult>singletonList(new PsiElementResolveResult(element, validResult));

            keepProcessing = false;
        }

        return keepProcessing;
    }
}
