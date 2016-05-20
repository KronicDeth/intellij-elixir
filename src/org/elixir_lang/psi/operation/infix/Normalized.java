package org.elixir_lang.psi.operation.infix;

import com.intellij.psi.PsiElement;
import org.elixir_lang.psi.Operator;
import org.elixir_lang.psi.Quotable;
import org.elixir_lang.psi.operation.Infix;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Canonical children of an {@link Infix}, which converts any errors in the operands to
 */
public class Normalized {
    /*
     * Static Methods
     */

    /*
     * Public Static Methods
     */

    @Contract(pure = true)
    @Nullable
    public static Quotable leftOperand(@NotNull Infix infix) {
        PsiElement[] children = infix.getChildren();
        int operatorIndex = operatorIndex(children);

        return leftOperand(children, operatorIndex);
    }

    @Contract(pure = true)
    @Nullable
    public static Quotable leftOperand(@NotNull PsiElement[] children, int operatorIndex) {
        Quotable leftOperand = null;
        int leftOperandCount = operatorIndex;

        // ensures that {@code partialLeft PsiErrorElement} returns {@code null}
        if (leftOperandCount == 1) {
            PsiElement child = children[operatorIndex - 1];

            // prevents PsiErrorElement from being leftOperand
            if (child instanceof Quotable) {
                leftOperand = (Quotable) child;
            }
        }

        return leftOperand;
    }

    @Contract(pure = true)
    @NotNull
    public static Operator operator(@NotNull Infix infix) {
        PsiElement[] children = infix.getChildren();
        int operatorIndex = operatorIndex(children);

        return operator(children, operatorIndex);
    }

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

    @Contract(pure = true)
    @Nullable
    public static Quotable rightOperand(@NotNull Infix infix) {
        PsiElement[] children = infix.getChildren();
        int operatorIndex = operatorIndex(children);

        return rightOperand(children, operatorIndex);
    }

    @Contract(pure = true)
    @Nullable
    public static Quotable rightOperand(@NotNull PsiElement[] children, int operatorIndex) {
        int rightOperandCount = children.length - 1 - operatorIndex;
        Quotable rightOperand = null;

        // ensure right operand is there and there isn't more than one
        if (rightOperandCount == 1) {
            PsiElement child = children[operatorIndex + 1];

            if (child instanceof Quotable) {
                rightOperand = (Quotable) child;
            }
        }

        return rightOperand;
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
