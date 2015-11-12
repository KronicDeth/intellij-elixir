package org.elixir_lang.psi;

import org.elixir_lang.psi.call.qualification.Qualified;

/**
 * <expression> dotInfixOperator relativeIdentifier matchedParenthesesArguments
 */
public interface QualifiedParenthesesCall extends Qualified, Quotable {
    ElixirMatchedParenthesesArguments getMatchedParenthesesArguments();
}
