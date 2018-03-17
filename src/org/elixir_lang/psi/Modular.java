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

import static org.elixir_lang.psi.impl.call.CallImplKt.macroChildCalls;
import static org.elixir_lang.structure_view.element.CallDefinitionClause.nameArityRange;

public class Modular {
    /*
     * Public Static Methods
     */

    public static boolean callDefinitionClauseCallWhile(@NotNull final Call modular,
                                                     @NotNull Function<Call, Boolean> function) {
        Call[] childCalls = macroChildCalls(modular);
        boolean keepProcessing = true;

        for (Call childCall : childCalls) {
            if (CallDefinitionClause.is(childCall) && !function.fun(childCall)) {
                keepProcessing = false;

                break;
            }
        }

        return keepProcessing;
    }

    public static void forEachCallDefinitionClauseNameIdentifier(
            @NotNull Call modular,
            @Nullable final String functionName,
            final int resolvedFinalArity,
            @NotNull final Function<PsiElement, Boolean> function
    ) {
        callDefinitionClauseCallWhile(
                modular,
                functionName,
                resolvedFinalArity,
                new Function<Call, Boolean>() {
                    @Override
                    public Boolean fun(Call call) {
                        boolean keepProcessing = true;

                        if (call instanceof Named) {
                            Named named = (Named) call;
                            PsiElement nameIdentifier = named.getNameIdentifier();

                            if (nameIdentifier != null && !function.fun(nameIdentifier)) {
                                keepProcessing = false;
                            }
                        }

                        return keepProcessing;
                    }
                }
        );
    }

    /*
     * Private Static Methods
     */

    private static void callDefinitionClauseCallWhile(@NotNull Call modular,
                                                               @Nullable final String functionName,
                                                               final int resolvedFinalArity,
                                                               @NotNull final Function<Call, Boolean> function) {
        if (functionName != null) {
            callDefinitionClauseCallWhile(
                    modular,
                    new Function<Call, Boolean>() {
                        @Override
                        public Boolean fun(@NotNull Call callDefinitionClauseCall) {
                            Pair<String, IntRange> nameArityRange = nameArityRange(callDefinitionClauseCall);
                            boolean keepProcessing = true;

                            if (nameArityRange != null) {
                                String name = nameArityRange.first;

                                if (name != null && name.equals(functionName)) {
                                    IntRange arityRange = nameArityRange.second;

                                    if (arityRange.containsInteger(resolvedFinalArity) &&
                                            !function.fun(callDefinitionClauseCall)) {
                                        keepProcessing = false;
                                    }
                                }
                            }

                            return keepProcessing;
                        }
                    }
            );
        }
    }
}
