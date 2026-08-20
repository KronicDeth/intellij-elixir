package org.elixir_lang.psi.operation;

import org.elixir_lang.psi.Quotable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

/**
 * A unary operator with a operator followed by operand.
 */
public interface Prefix extends Operation {
    /**
     * @return {@code null} if there is an error in the operand or it is missing
     */
    @Contract(pure = true)
    @Nullable
    Quotable operand();
}
