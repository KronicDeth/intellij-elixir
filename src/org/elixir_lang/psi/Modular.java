package org.elixir_lang.psi;

import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiElement;
import com.intellij.util.Function;
import org.apache.commons.lang.math.IntRange;
import org.elixir_lang.psi.call.Call;
import org.elixir_lang.psi.call.Named;
import org.elixir_lang.structure_view.element.CallDefinitionClause;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.elixir_lang.psi.impl.ElixirPsiImplUtil.macroChildCalls;
import static org.elixir_lang.structure_view.element.CallDefinitionClause.nameArityRange;

public class Modular {
    /*
     * Public Static Methods
     */

    public static <Result> void forEachCallDefinitionClauseNameIdentifier(
            @NotNull Call modular,
            @Nullable final String functionName,
            final int resolvedFinalArity,
            @NotNull final Function<PsiElement, Result> function
    ) {
        forEachCallDefinitionClauseCall(
                modular,
                functionName,
                resolvedFinalArity,
                new Function<Call, Object>() {
                    @Override
                    public Object fun(Call call) {
                        if (call instanceof Named) {
                            Named named = (Named) call;
                            PsiElement nameIdentifier = named.getNameIdentifier();

                            if (nameIdentifier != null) {
                                function.fun(nameIdentifier);
                            }
                        }


                        return null;
                    }
                }
        );
    }

    /*
     * Private Static Methods
     */

    /**
     * @param modular
     */
    private static <Result> void forEachCallDefinitionClauseCall(@NotNull final Call modular,
                                                                 @NotNull Function<Call, Result> function) {
        Call[] childCalls = macroChildCalls(modular);

        if (childCalls != null) {
            for (Call childCall : childCalls) {
                if (CallDefinitionClause.is(childCall)) {
                    function.fun(childCall);
                }
            }
        }
    }

    private static <Result> void forEachCallDefinitionClauseCall(@NotNull Call modular,
                                                                 @Nullable final String functionName,
                                                                 final int resolvedFinalArity,
                                                                 @NotNull final Function<Call, Result> function) {
        if (functionName != null) {
            forEachCallDefinitionClauseCall(
                    modular,
                    new Function<Call, Object>() {
                        @Override
                        public Object fun(@NotNull Call callDefinitionClauseCall) {
                            Pair<String, IntRange> nameArityRange = nameArityRange(callDefinitionClauseCall);

                            if (nameArityRange != null) {
                                String name = nameArityRange.first;

                                if (name != null && name.equals(functionName)) {
                                    IntRange arityRange = nameArityRange.second;

                                    if (arityRange.containsInteger(resolvedFinalArity)) {
                                        function.fun(callDefinitionClauseCall);
                                    }
                                }
                            }

                            return null;
                        }
                    }
            );
        }
    }
}
