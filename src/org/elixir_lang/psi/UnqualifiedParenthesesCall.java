package org.elixir_lang.psi;

import org.elixir_lang.psi.call.Call;
import org.elixir_lang.psi.call.arguments.Parentheses;
import org.elixir_lang.psi.qualification.Unqualified;

/**
 * {@code IDENTIFIER matchedParenthesesArguments}
 */
public interface UnqualifiedParenthesesCall extends Call, Parentheses, Quotable, Unqualified {
}
