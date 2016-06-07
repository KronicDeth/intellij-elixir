package org.elixir_lang.psi.operation;

import com.intellij.psi.PsiElement;
import org.elixir_lang.psi.Operator;
import org.elixir_lang.psi.operation.Operation;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Canonical children of an {@link Operation}, which converts any errors to `null`.
 */
public class Normalized {
    /*
     *
     * Static Methods
     *
     */

    /*
     * Public Static Methods
     */

    @Contract(pure = true)
    @NotNull
    public static Operator operator(@NotNull Operation operation) {
        PsiElement[] children = operation.getChildren();
        int operatorIndex = operatorIndex(children);

        return operator(children, operatorIndex);
    }

    @Contract(pure = true)
    public static int operatorIndex(PsiElement[] children) {
        int operatorIndex = -1;
        for (int i = 0; i < children.length; i++) {
            if (children[i] instanceof Operator) {
                operatorIndex = i;
                break;
            }
        }

        assert operatorIndex != -1;

        return operatorIndex;
    }

    /*
     * Private Static Methods
     */

    @Contract(pure = true)
    @NotNull
    private static Operator operator(@NotNull PsiElement[] children, int operatorIndex) {
        return (Operator) children[operatorIndex];
    }
}
