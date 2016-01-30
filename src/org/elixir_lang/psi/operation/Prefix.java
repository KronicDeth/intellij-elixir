package org.elixir_lang.psi.operation;

import org.elixir_lang.psi.Quotable;

/**
 * A unary operator with a operator followed by operand.
 *
 * Created by luke.imhoff on 3/19/15.
 */
public interface Prefix extends Operation {
    Quotable operand();
}
