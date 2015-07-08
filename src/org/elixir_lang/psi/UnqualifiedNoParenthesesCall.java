package org.elixir_lang.psi;

/**
 * IDENTIFIER noParenthesesOneArgument
 */
public interface UnqualifiedNoParenthesesCall extends Quotable {
    ElixirDoBlock getDoBlock();

    ElixirNoParenthesesOneArgument getNoParenthesesOneArgument();
}
