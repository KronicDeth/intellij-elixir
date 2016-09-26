package org.elixir_lang.psi.scope.call_definition_clause;

import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.util.PsiTreeUtil;
import gnu.trove.THashSet;
import org.apache.commons.lang.math.IntRange;
import org.elixir_lang.psi.call.Call;
import org.elixir_lang.psi.call.Named;
import org.elixir_lang.structure_view.element.CallDefinitionClause;
import org.elixir_lang.structure_view.element.modular.Module;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

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
    private Set<PsiElement> resolvedSet = null;
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

    private boolean addToResolveResultList(@NotNull Call call, boolean validResult, ResolveState state) {
        boolean keepProcessing = true;

        if (call instanceof  Named) {
            Named named = (Named) call;
            PsiElement nameIdentifier = named.getNameIdentifier();

            if (nameIdentifier != null) {
                /* call definition clause needs to not have a self-reference, so that OpenAPI uses Find Usages
                   instead */
                if (PsiTreeUtil.isAncestor(state.get(ENTRANCE), nameIdentifier, false)) {
                    keepProcessing = false;
                } else {
                /* Doesn't use a Map<PsiElement, ResolveSet> so that MultiResolve's helpers that require a
                   List<ResolveResult> can still work */
                    if (resolvedSet == null || !resolvedSet.contains(nameIdentifier)) {
                        resolveResultList = org.elixir_lang.psi.scope.MultiResolve.addToResolveResultList(
                                resolveResultList, new PsiElementResolveResult(nameIdentifier, validResult)
                        );

                        if (resolvedSet == null) {
                            resolvedSet = new THashSet<PsiElement>();
                        }

                        resolvedSet.add(nameIdentifier);

                    }
                }
            }
        }

        return keepProcessing;
    }

    private boolean execute(@NotNull Call element, @NotNull ResolveState state) {
        boolean keepProcessing = true;

        if (CallDefinitionClause.is(element)) {
            Pair<String, IntRange> nameArityRange = nameArityRange(element);

            if (nameArityRange != null) {
                String name = nameArityRange.first;

                if (name.equals(this.name)) {
                    IntRange arityRange = nameArityRange.second;

                    if (arityRange.containsInteger(resolvedFinalArity)) {
                        keepProcessing = addToResolveResultList(element, true, state);
                    } else if (incompleteCode) {
                        keepProcessing = addToResolveResultList(element, false, state);
                    }
                } else if (incompleteCode && name.startsWith(this.name)) {
                    keepProcessing = addToResolveResultList(element, false, state);
                }

                // Don't check MultiResolve.keepProcessing in case recursive call of function with multiple clauses
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

            // Only check MultiResolve.keepProcessing at the end of a Module to all multiple arities
            keepProcessing = org.elixir_lang.psi.scope.MultiResolve.keepProcessing(incompleteCode, resolveResultList);
        }

        return keepProcessing;
    }
}
