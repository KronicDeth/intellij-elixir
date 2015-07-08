package org.elixir_lang.psi;

/**
 * <expression> dotInfixOperator relativeIdentifier matchedParenthesesArguments
 */
public interface QualifiedParenthesesCall extends Quotable {
    ElixirDoBlock getDoBlock();

    ElixirMatchedParenthesesArguments getMatchedParenthesesArguments();

    Quotable getRelativeIdentifier();
}
