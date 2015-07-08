package org.elixir_lang.psi;

/**
 * IDENTIFIER matchedParenthesesArguments
 */
public interface UnqualifiedParenthesesCall extends Quotable {
    ElixirDoBlock getDoBlock();

    ElixirMatchedParenthesesArguments getMatchedParenthesesArguments();
}
