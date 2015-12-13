package org.elixir_lang.psi;

/**
 * A binary operator with a left operand, operator, and right operand
 *
 * Created by luke.imhoff on 3/18/15.
 */
public interface InfixOperation extends Quotable {
  Operator operator();
  Quotable leftOperand();
  Quotable rightOperand();
}
