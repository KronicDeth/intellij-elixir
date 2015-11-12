package org.elixir_lang.psi;

import org.elixir_lang.psi.call.Call;

/**
 * IDENTIFIER matchedParenthesesArguments
 */
public interface UnqualifiedParenthesesCall extends Call, Quotable {
    ElixirMatchedParenthesesArguments getMatchedParenthesesArguments();
}
