package org.elixir_lang.psi;

/**
 * <expression> dotInfixOperator relativeIdentifier !CALL
 */
public interface QualifiedNoArgumentsCall extends Call, Quotable {
    Quotable getRelativeIdentifier();
}
