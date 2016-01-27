package org.elixir_lang.psi;

/**
 * A binary operator with a left operand, association operator, and right operand, but the association operator is
 * ignored for quoting, so not an {@link InfixOperation}.
 */
public interface AssociationOperation extends Quotable {
}
