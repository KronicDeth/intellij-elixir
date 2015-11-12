package org.elixir_lang.psi;

import org.elixir_lang.psi.call.Call;

/**
 * <expression> dotInfixOperator relativeIdentifier !CALL
 */
public interface QualifiedNoArgumentsCall extends Call, Quotable {
    Quotable getRelativeIdentifier();
}
