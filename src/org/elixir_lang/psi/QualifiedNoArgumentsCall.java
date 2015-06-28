package org.elixir_lang.psi;

/**
 * <expression> dotInfixOperator relativeIdentifier !CALL
 */
public interface QualifiedNoArgumentsCall extends Quotable {
    ElixirDoBlock getDoBlock();

    Quotable getRelativeIdentifier();
}
