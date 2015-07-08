package org.elixir_lang.psi;

/**
 * <expression> dotInfixOperator relativeIdentifier noParenthesesOneArgument
 */
public interface QualifiedNoParenthesesCall extends Quotable {
    ElixirDoBlock getDoBlock();

    ElixirNoParenthesesOneArgument getNoParenthesesOneArgument();

    Quotable getRelativeIdentifier();
}
