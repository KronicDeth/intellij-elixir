package org.elixir_lang.psi.operation;

import org.elixir_lang.psi.Operator;
import org.elixir_lang.psi.Quotable;

public interface Operation extends Quotable {
    Operator operator();
}
