package org.elixir_lang.psi;

/**
 * IDENTIFIER matchedParenthesesArguments
 */
public interface UnqualifiedParenthesesCall extends Call, Quotable {
    ElixirMatchedParenthesesArguments getMatchedParenthesesArguments();
}
