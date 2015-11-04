package org.elixir_lang.psi;

import org.jetbrains.annotations.NotNull;

/**
 * IDENTIFIER noParenthesesOneArgument
 */
public interface UnqualifiedNoParenthesesCall extends Quotable {
    @NotNull
    String functionName();

    ElixirDoBlock getDoBlock();

    ElixirNoParenthesesOneArgument getNoParenthesesOneArgument();
}
