package org.elixir_lang.psi.operation.infix;

import com.intellij.psi.PsiElement;
import org.elixir_lang.psi.Operator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

import static com.intellij.psi.util.PsiTreeUtil.isAncestor;
import static org.elixir_lang.psi.operation.Normalized.operatorIndex;

/**
 * Partitions children into those left of the operator, the operator, and those right of the operator.  This will
 * include any error elements and the left or right children may be empty if there are other parsing errors with
 * recovery.
 */
public class Triple {
    /*
     * Fields
     */

    @NotNull
    public final PsiElement[] leftOperands;
    @NotNull
    public final PsiElement operator;
    @NotNull
    public final PsiElement[] rightOperands;

    /*
     * Constructors
     */

    public Triple(@NotNull PsiElement[] children) {
        this(children, operatorIndex(children));
    }

    private Triple(@NotNull PsiElement[] children, int operatorIndex) {
        this(
                Arrays.copyOfRange(children, 0, operatorIndex),
                (Operator) children[operatorIndex],
                Arrays.copyOfRange(children, operatorIndex + 1, children.length)
        );
    }

    private Triple(@NotNull PsiElement[] leftOperands, @NotNull Operator operator, @NotNull PsiElement[] rightOperands) {
        this.leftOperands = leftOperands;
        this.operator = operator;
        this.rightOperands = rightOperands;
    }

    /*
     *
     * Instance Methods
     *
     */

    /**
     *
     * @param element an element that MAY be an descendant of a child of this triple
     * @return {@code null} if element is not a descendant of any children in this Triple.
     */
    @Nullable
    public Position ancestorPosition(@NotNull PsiElement element) {
        Position position = null;

        if (isAnyAncestor(leftOperands, element)) {
            position = Position.LEFT;
        } else if (isAnyAncestor(rightOperands, element)) {
            position = Position.RIGHT;
        } else if (isAncestor(operator, element, false)) {
            position = Position.OPERATOR;
        }

        return position;
    }

    /*
     * Private Instance Methods
     */

    private boolean isAnyAncestor(@NotNull PsiElement[] operands, @NotNull PsiElement element) {
        boolean isAnyAncestor = false;

        for (PsiElement operand : operands) {
            if (isAncestor(operand, element, false)) {
                isAnyAncestor = true;

                break;
            }
        }

        return isAnyAncestor;
    }
}
