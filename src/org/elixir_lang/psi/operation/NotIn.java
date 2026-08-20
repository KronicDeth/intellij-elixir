package org.elixir_lang.psi.operation;

import org.elixir_lang.psi.ElixirInInfixOperator;
import org.elixir_lang.psi.ElixirNotInfixOperator;
import org.elixir_lang.psi.Quotable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * <expression> notInfixOperator inInfixOperator <expression>
 */
public interface NotIn extends Quotable {
    /**
     * @return {@code null} if there was an error in element (such as when user is till typing it) and so the Pratt Parser
     *   error handling matches {@code partialLeft PsiElementError operand rightOperand}.  {@code partialLeft} is not
     *   returned because it is often incorrect, like the {@code def foo() do} above {@code = call()} when introducing a
     *   local variable.
     */
    @Nullable
    Quotable leftOperand();

    @NotNull
    ElixirInInfixOperator getInInfixOperator();

    @NotNull
    ElixirNotInfixOperator getNotInfixOperator();

    /**
     * Can't be Quotable like {@link #leftOperand()} because it may be a {@link com.intellij.psi.PsiErrorElement}.
     *
     * @return {@code null} if there was an error in element (such as when the user is still typing it) and so the
     *   Pratt Parser error handling matched up through the operator, but was ok not having the right-operand; otherwise,
     *   the operand to the right of the operator.  May also return {@code null} if the element is there, but is a
     *   {@link com.intellij.psi.PsiErrorElement}.
     */
    @Nullable
    Quotable rightOperand();
}
