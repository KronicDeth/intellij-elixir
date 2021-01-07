package org.elixir_lang.psi.operation.not_in;

import com.intellij.psi.PsiElement;
import org.elixir_lang.psi.Operator;
import org.elixir_lang.psi.Quotable;
import org.elixir_lang.psi.operation.NotIn;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Canonical children of an {@link org.elixir_lang.psi.operation.NotIn}, which converts any errors in the operands to `null`
 */
public class Normalized {
    @Contract(pure = true)
    @Nullable
    public static Quotable leftOperand(@NotNull NotIn notIn) {
        PsiElement[] children = notIn.getChildren();
        return leftOperand(children);
    }

    @Contract(pure = true)
    @Nullable
    public static Quotable leftOperand(@NotNull PsiElement[] children) {
        int leftOperatorIndex = leftOperatorIndex(children);

        return leftOperand(children, leftOperatorIndex);
    }

    @Contract(pure = true)
    @Nullable
    public static Quotable leftOperand(@NotNull PsiElement[] children, int leftOperatorIndex) {
        Quotable leftOperand = null;
        int leftOperandCount = leftOperatorIndex;

        if (leftOperandCount == 1) {
            PsiElement child = children[leftOperatorIndex - 1];

            if (child instanceof Quotable) {
                leftOperand = (Quotable) child;
            }
        }

        return leftOperand;
    }

    @Contract(pure = true)
    @NotNull
    public static int leftOperatorIndex(@NotNull PsiElement[] children) {
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
    public static Quotable rightOperand(@NotNull NotIn notIn) {
        PsiElement[] children = notIn.getChildren();

        return rightOperand(children);
    }

    @Contract(pure = true)
    @Nullable
    public static Quotable rightOperand(@NotNull PsiElement[] children) {
        int rightOperatorIndex = rightOperatorIndex(children);

        return rightOperand(children, rightOperatorIndex);
    }

    @Contract(pure = true)
    @Nullable
    public static Quotable rightOperand(@NotNull PsiElement[] children, int rightOperandIndex) {
        int rightOperandCount = children.length - 1 - rightOperandIndex;
        Quotable rightOperand = null;

        if (rightOperandCount == 1) {
            PsiElement child = children[rightOperandIndex + 1];

            if (child instanceof Quotable) {
                rightOperand = (Quotable) child;
            }
        }

        return rightOperand;
    }

    @Contract(pure = true)
    @NotNull
    public static int rightOperatorIndex(@NotNull PsiElement[] children) {
        int operatorIndex = -1;

        for (int i = children.length - 1; i >= 0; i--) {
            if (children[i] instanceof Operator) {
                operatorIndex = i;
                break;
            }
        }

        assert operatorIndex != -1;

        return operatorIndex;
    }
}
