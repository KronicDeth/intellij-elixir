package org.elixir_lang.psi.operation;

import org.elixir_lang.psi.Quotable;

/**
 * A binary operator with a left operand, operator, and right operand
 *
 * Created by luke.imhoff on 3/18/15.
 */
public interface Infix extends Operation {
  Quotable leftOperand();
  Quotable rightOperand();
}
