package org.elixir_lang.psi.scope;

import com.intellij.openapi.util.Condition;
import com.intellij.psi.ResolveResult;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MultiResolve {
    /*
     * Constants
     */

    public static final Condition<ResolveResult> HAS_VALID_RESULT_CONDITION = new Condition<ResolveResult>() {
        @Override
        public boolean value(ResolveResult resolveResult) {
            return resolveResult.isValidResult();
        }
    };

    /*
     * Public Static Methods
     */

    public static List<ResolveResult> addToResolveResultList(@Nullable List<ResolveResult> resolveResultList,
                                                             @NotNull ResolveResult resolveResult) {
        if (resolveResultList == null) {
            resolveResultList = new ArrayList<ResolveResult>();
        }

        resolveResultList.add(resolveResult);

        return resolveResultList;
    }

    /**
     * Whether the {@code resolveResultList} has any {@link ResolveResult} where {@link ResolveResult#isValidResult()}
     * is {@code true}.
     *
     * @return {@code false} if {@code resolveResultList} is {@code null}; otherwise, {@code true} if the
     * {@code resolveResultList} has any {@link ResolveResult} where {@link ResolveResult#isValidResult()} is
     * {@code true}.
     */
    public static boolean hasValidResult(@Nullable List<ResolveResult> resolveResultList) {
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
     * @return {@code false} if {@link #hasValidResult(List)} or {@code incompleteCode} is {@code false}, so only one
     * valid result is allowed.
     */
    public static boolean keepProcessing(boolean incompleteCode, @Nullable List<ResolveResult> resolveResultList) {
        return incompleteCode || !hasValidResult(resolveResultList);
    }
}
