package org.elixir_lang.psi;

import org.elixir_lang.psi.call.Call;

/**
 * <expression> dotInfixOperator relativeIdentifier matchedParenthesesArguments
 */
public interface QualifiedParenthesesCall extends Call, Quotable {
    ElixirMatchedParenthesesArguments getMatchedParenthesesArguments();

    Quotable getRelativeIdentifier();
}
