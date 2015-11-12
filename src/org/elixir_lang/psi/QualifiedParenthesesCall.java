package org.elixir_lang.psi;

import org.elixir_lang.psi.call.Call;
import org.elixir_lang.psi.qualification.Qualified;

/**
 * <expression> dotInfixOperator relativeIdentifier matchedParenthesesArguments
 */
public interface QualifiedParenthesesCall extends Call, Qualified, Quotable {
    ElixirMatchedParenthesesArguments getMatchedParenthesesArguments();
}
