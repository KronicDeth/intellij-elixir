package org.elixir_lang.psi;

import org.elixir_lang.psi.call.Call;

/**
 * atPrefixOperator IDENTIFIER CALL bracketArguments
 */
public interface AtUnqualifiedNoParenthesesCall extends Call, Quotable {
    Quotable getAtPrefixOperator();

    QuotableArguments getNoParenthesesOneArgument();
}
