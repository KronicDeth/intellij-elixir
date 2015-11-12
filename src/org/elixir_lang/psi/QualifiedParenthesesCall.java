package org.elixir_lang.psi;

/**
 * <expression> dotInfixOperator relativeIdentifier matchedParenthesesArguments
 */
public interface QualifiedParenthesesCall extends Call, Quotable {
    ElixirMatchedParenthesesArguments getMatchedParenthesesArguments();

    Quotable getRelativeIdentifier();
}
