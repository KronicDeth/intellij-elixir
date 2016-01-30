package org.elixir_lang.psi;

import org.elixir_lang.psi.operation.Infix;

/**
 * A binary operator with a left operand, association operator, and right operand, but the association operator is
 * ignored for quoting, so not an {@link Infix}.
 */
public interface AssociationOperation extends Quotable {
}
