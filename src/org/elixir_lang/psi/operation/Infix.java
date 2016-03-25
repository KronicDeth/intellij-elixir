package org.elixir_lang.psi.operation;

import org.elixir_lang.psi.Quotable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A binary operator with a left operand, operator, and right operand
 *
 * Created by luke.imhoff on 3/18/15.
 */
public interface Infix extends Operation {
  @NotNull
  Quotable leftOperand();

  /**
   *
   * @return {@code null} if there was an error in element (such as when the user is still typing it) and so the
   *   Pratt Parser error handling matched up through the operator, but was ok not having the right-operand; otherwise,
   *   the operand to the right of the operator.
   */
  @Nullable
  Quotable rightOperand();
}
