package org.elixir_lang.psi;

/**
 * atPrefixOperator IDENTIFIER CALL bracketArguments
 */
public interface AtUnqualifiedBracketOperation extends Quotable {
    Quotable getAtPrefixOperator();

    Quotable getBracketArguments();
}
