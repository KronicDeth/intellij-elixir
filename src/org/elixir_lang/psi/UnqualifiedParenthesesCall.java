package org.elixir_lang.psi;

import org.elixir_lang.psi.call.Call;
import org.elixir_lang.psi.qualification.Unqualified;

/**
 * IDENTIFIER matchedParenthesesArguments
 */
public interface UnqualifiedParenthesesCall extends Call, Quotable, Unqualified {
    ElixirMatchedParenthesesArguments getMatchedParenthesesArguments();
}
