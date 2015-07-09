package org.elixir_lang.psi;

/**
 * <expression> dotInfixOperator relativeIdentifier CALL bracketArguments
 */
public interface QualifiedBracketOperation extends Quotable {
    ElixirRelativeIdentifier getRelativeIdentifier();

    Quotable getBracketArguments();
}
