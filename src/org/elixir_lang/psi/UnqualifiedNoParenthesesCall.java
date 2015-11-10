package org.elixir_lang.psi;

import org.jetbrains.annotations.NotNull;

/**
 * IDENTIFIER noParenthesesOneArgument
 */
public interface UnqualifiedNoParenthesesCall extends Call, Quotable {
    @NotNull
    String functionName();

    ElixirNoParenthesesOneArgument getNoParenthesesOneArgument();
}
