package org.elixir_lang.psi;

/**
 * atPrefixOperator IDENTIFIER CALL bracketArguments
 */
public interface AtUnqualifiedNoParenthesesCall extends Call, Quotable {
    Quotable getAtPrefixOperator();

    QuotableArguments getNoParenthesesOneArgument();
}
