package org.elixir_lang.psi.scope.call_definition_clause;

import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.util.PsiTreeUtil;
import org.apache.commons.lang.math.IntRange;
import org.elixir_lang.psi.call.Call;
import org.elixir_lang.psi.call.Named;
import org.elixir_lang.structure_view.element.CallDefinitionClause;
import org.elixir_lang.structure_view.element.modular.Module;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static org.elixir_lang.psi.impl.ElixirPsiImplUtil.ENTRANCE;
import static org.elixir_lang.psi.impl.ElixirPsiImplUtil.macroChildCalls;
import static org.elixir_lang.structure_view.element.CallDefinitionClause.nameArityRange;

public class MultiResolve implements PsiScopeProcessor {
    /*
     * Public Static Methods
     */

    @Nullable
    public static List<ResolveResult> resolveResultList(@NotNull String name,
                                                        int resolvedFinalArity,
                                                        boolean incompleteCode,
                                                        @NotNull PsiElement entrance) {
        return resolveResultList(name, resolvedFinalArity, incompleteCode, entrance, ResolveState.initial());
    }

    @Nullable
    public static List<ResolveResult> resolveResultList(@NotNull String name,
                                                        int resolvedFinalArity,
                                                        boolean incompleteCode,
                                                        @NotNull PsiElement entrance,
                                                        @NotNull ResolveState resolveState) {
        MultiResolve multiResolve = new MultiResolve(name, resolvedFinalArity, incompleteCode);
        PsiTreeUtil.treeWalkUp(
                multiResolve,
                entrance,
                entrance.getContainingFile(),
                resolveState.put(ENTRANCE, entrance)
        );
        return multiResolve.getResolveResultList();
    }

    /*
     * Fields
     */

    private final boolean incompleteCode;
    @NotNull
    private final String name;
    private final int resolvedFinalArity;
    @Nullable
    private List<ResolveResult> resolveResultList = null;

    /*
     * Constructors
     */

    private MultiResolve(@NotNull String name, int resolvedFinalArity, boolean incompleteCode) {
        this.incompleteCode = incompleteCode;
        this.name = name;
        this.resolvedFinalArity = resolvedFinalArity;
    }

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

    /*
     * Private Instance Methods
     */

    private void addToResolveResultList(@NotNull Call call, boolean validResult) {
        if (call instanceof  Named) {
            Named named = (Named) call;
            PsiElement nameIdentifier = named.getNameIdentifier();

            if (nameIdentifier != null) {
                resolveResultList = org.elixir_lang.psi.scope.MultiResolve.addToResolveResultList(
                        resolveResultList, new PsiElementResolveResult(nameIdentifier, validResult)
                );
            }
        }
    }

    private boolean execute(@NotNull Call element, @NotNull @SuppressWarnings("unused") ResolveState state) {
        if (CallDefinitionClause.is(element)) {
            Pair<String, IntRange> nameArityRange = nameArityRange(element);

            if (nameArityRange != null) {
                String name = nameArityRange.first;

                if (name.equals(this.name)) {
                    IntRange arityRange = nameArityRange.second;

                    if (arityRange.containsInteger(resolvedFinalArity)) {

                        addToResolveResultList(element, true);
                    } else if (incompleteCode) {
                        addToResolveResultList(element, false);
                    }
                } else if (incompleteCode && name.startsWith(this.name)) {
                    addToResolveResultList(element, false);
                }
            }
        } else if (Module.is(element)) {
            Call[] childCalls = macroChildCalls(element);

            if (childCalls != null) {
                for (Call childCall : childCalls) {
                    if(!execute(childCall, state)) {
                        break;
                    }
                }
            }
        }

        return org.elixir_lang.psi.scope.MultiResolve.keepProcessing(incompleteCode, resolveResultList);
    }
}
