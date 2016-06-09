package org.elixir_lang.psi.operation.prefix;

import com.intellij.psi.PsiElement;
import org.elixir_lang.psi.Quotable;
import org.elixir_lang.psi.operation.Prefix;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.elixir_lang.psi.operation.Normalized.operatorIndex;

/**
 * Canonical children of a {@link Prefix}, which converts an errors in the operands to `null`
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
    @Nullable
    public static Quotable operand(@NotNull Prefix prefix) {
        PsiElement[] children = prefix.getChildren();
        int operatorIndex = operatorIndex(children);

        return operand(children, operatorIndex);
    }

    @Contract(pure = true)
    @Nullable
    private static Quotable operand(@NotNull PsiElement[] children, int operatorIndex) {
        int operandCount = children.length - 1 - operatorIndex;
        Quotable operand = null;

        // ensure operand is there and there isn't more than one
        if (operandCount == 1) {
            PsiElement child = children[operatorIndex + 1];

            if (child instanceof Quotable) {
                operand = (Quotable) child;
            }
        }

        return operand;
    }
}
