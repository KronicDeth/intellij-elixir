package org.elixir_lang.psi;

/**
 * atPrefixOperator numeric CALL bracketArguments
 */
public interface AtNumericBracketOperation extends Quotable {
    Quotable getAtPrefixOperator();

    Quotable getBracketArguments();
}
