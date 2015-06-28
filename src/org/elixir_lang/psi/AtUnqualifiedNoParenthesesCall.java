package org.elixir_lang.psi;

/**
 * atPrefixOperator IDENTIFIER CALL bracketArguments
 */
public interface AtUnqualifiedNoParenthesesCall extends Quotable {
    Quotable getAtPrefixOperator();

    ElixirDoBlock getDoBlock();

    QuotableArguments getNoParenthesesOneArgument();
}
