package org.elixir_lang.psi.scope.variable;

import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.Key;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.containers.ContainerUtil;
import org.elixir_lang.psi.*;
import org.elixir_lang.psi.call.Call;
import org.elixir_lang.psi.impl.ElixirPsiImplUtil;
import org.elixir_lang.psi.operation.Match;
import org.elixir_lang.psi.scope.Variable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.elixir_lang.psi.impl.ElixirPsiImplUtil.DECLARING_SCOPE;
import static org.elixir_lang.psi.impl.ElixirPsiImplUtil.ENTRANCE;
import static org.elixir_lang.reference.Callable.IGNORED;

/**
 * Finds
 */
public class MultiResolve extends Variable {
    /*
     * CONSTANTS
     */

    private static final Key<PsiElement> LAST_BINDING_KEY = new Key<PsiElement>("LAST_BINDING_KEY");
    private static final Condition<ResolveResult> HAS_VALID_RESULT_CONDITION = new Condition<ResolveResult>() {
        @Override
        public boolean value(ResolveResult resolveResult) {
            return resolveResult.isValidResult();
        }
    };

    /*
     *
     * Static Methods
     *
     */

    /*
     * Public Static Methods
     */
    @NotNull
    private final String name;
    private final boolean incompleteCode;

    /*
     * Private Static Methods
     */
    @Nullable
    private List<ResolveResult> resolveResultList = null;

    public MultiResolve(@NotNull String name, boolean incompleteCode) {
        this.incompleteCode = incompleteCode;
        this.name = name;
    }

    @Nullable
    public static List<ResolveResult> resolveResultList(@NotNull String name,
                                                        boolean incompleteCode,
                                                        @NotNull PsiElement entrance) {
        List<ResolveResult> resolveResultList;

        if (name.equals(IGNORED)) {
            resolveResultList = Collections.<ResolveResult>singletonList(new PsiElementResolveResult(entrance));
        } else {
            resolveResultList = resolveResultList(name, incompleteCode, entrance, ResolveState.initial());
        }

        return resolveResultList;
    }

    /*
     * Fields
     */

    @Nullable
    public static List<ResolveResult> resolveResultList(@NotNull String name,
                                                        boolean incompleteCode,
                                                        @NotNull PsiElement entrance,
                                                        @NotNull ResolveState resolveState) {
        MultiResolve multiResolve = new MultiResolve(name, incompleteCode);
        PsiTreeUtil.treeWalkUp(
                multiResolve,
                entrance,
                entrance.getContainingFile(),
                resolveState.put(ENTRANCE, entrance)
        );
        return multiResolve.getResolveResultList();
    }

    @Contract(pure = true)
    @Nullable
    private static PsiElement previousExpression(@NotNull PsiElement element) {
        PsiElement expression = ElixirPsiImplUtil.previousSiblingExpression(element);

        if (expression == null) {
            expression = previousParentExpression(element);
        }

        return expression;
    }

    @Contract(pure = true)
    @Nullable
    private static PsiElement previousParentExpression(@NotNull PsiElement element) {
        PsiElement expression = element;

        do {
            expression = expression.getParent();
        } while (expression instanceof ElixirDoBlock ||
                expression instanceof ElixirStab ||
                expression instanceof ElixirStabBody);

        return expression;
    }

    /*
     *
     * Instance Methods
     *
     */

    /*
     * Public Instance Methods
     */

    @Nullable
    public List<ResolveResult> getResolveResultList() {
        return resolveResultList;
    }

    /*
     * Protected Instance Methods
     */

    /**
     * Decides whether {@code match} matches the criteria being searched for.  All other {@link #executeOnVariable} methods
     * eventually end here.
     *
     * @param match
     * @param state
     */
    @Override
    protected boolean executeOnVariable(@NotNull PsiNamedElement match, @NotNull ResolveState state) {
        addToResolveResultListIfMatchingName(match, state);

        return keepProcessing();
    }

    /*
     * Private Instance Methods
     */

    /**
     * Adds {@code resolveResult} to {@link #resolveResultList} if it exists; otherwise, create new
     * {@code List<ResolveList>}, set {@link #resolveResultList} to it, and add {@code resolveResult} to it.
     *
     * @param resolveResult The element to add to {@code resolveResultList}
     */
    private void addToResolveResultList(@NotNull ResolveResult resolveResult) {
        if (resolveResultList == null) {
            resolveResultList = new ArrayList<ResolveResult>();
        }

        resolveResultList.add(resolveResult);
    }

    private void addToResolveResultList(@NotNull PsiElement element, ResolveState state, boolean validResult) {
        Boolean declaringScope = state.get(DECLARING_SCOPE);

        if (declaringScope == null || declaringScope) {
            PsiElement lastBinding = state.get(LAST_BINDING_KEY);
            boolean added = false;

            /* if LAST_BINDING_KEY is set, then we're checking if a right-hand match is bound higher up, so an effective
               recursive call.  If the recursive call got the same result, stop the recursion by not checking for
               rebinding */
            if (lastBinding == null || !element.isEquivalentTo(lastBinding)) {
                if (!(element instanceof Call) ||
                        !isInDeclaringScope((Call) element, state) ||
                        /* if maybe a macro, then it could potentially not be a macro, in which case prefer the early
                           declaration */
                        Boolean.TRUE.equals(state.get(MAYBE_MACRO))) {
                    Match matchAncestor = PsiTreeUtil.getContextOfType(element, Match.class);

                    if (matchAncestor != null) {
                        PsiElement rightOperand = matchAncestor.rightOperand();

                        if (rightOperand != null) {
                        /* right-hand match can only be declarative if it is not already bound, so need to try to
                           resolve further up to try to find if {@code element} is already bound */
                            if (PsiTreeUtil.isAncestor(rightOperand, element, false)) {
                                // previous sibling or parent to search for earlier binding
                                PsiElement expression = previousExpression(matchAncestor);

                                if (expression != null) {
                                    List<ResolveResult> preboundResolveResultList = resolveResultList(
                                            name,
                                            incompleteCode,
                                            expression,
                                            ResolveState.initial().put(LAST_BINDING_KEY, element)
                                    );

                                    if (preboundResolveResultList != null && preboundResolveResultList.size() > 0) {
                                        if (resolveResultList == null) {
                                            resolveResultList = preboundResolveResultList;
                                        } else {
                                            resolveResultList.addAll(preboundResolveResultList);
                                        }

                                        added = true;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // either non-right match declaration or recursive call didn't find a rebinding
            if (!added) {
                addToResolveResultList(new PsiElementResolveResult(element, validResult));
            }
        }
    }

    private void addToResolveResultListIfMatchingName(@NotNull PsiNamedElement match, ResolveState state) {
        String matchName = match.getName();

        if (matchName != null) {
            if (matchName.equals(name)) {
                addToResolveResultList(match, state, true);
            } else if (incompleteCode && matchName.startsWith(name)) {
                addToResolveResultList(match, state, false);
            }
        }
    }

    /**
     * Whether the {@link #resolveResultList} has any {@link ResolveResult} where {@link ResolveResult#isValidResult()}
     * is {@code true}.
     *
     * @return {@code false} if {@link #resolveResultList} is {@code null}; otherwise, {@code true} if the
     * {@link #resolveResultList} has any {@link ResolveResult} where {@link ResolveResult#isValidResult()} is
     * {@code true}.
     */
    private boolean hasValidResult() {
        boolean hasValidResult = false;

        if (resolveResultList != null) {
            hasValidResult = ContainerUtil.exists(resolveResultList, HAS_VALID_RESULT_CONDITION);
        }

        return hasValidResult;
    }

    /**
     * Keep trying to resolve the reference if {@code resolveResultList} does not have a valid result or the code is
     * incomplete.
     *
     * @return {@code false} if {@link #hasValidResult()} or {@link #incompleteCode} is {@code false}, so only one
     * valid result is allowed.
     */
    private boolean keepProcessing() {
        return incompleteCode || !hasValidResult();
    }
}
