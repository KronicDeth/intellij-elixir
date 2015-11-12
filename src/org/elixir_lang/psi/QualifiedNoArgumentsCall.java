package org.elixir_lang.psi;

import org.elixir_lang.psi.call.Call;
import org.elixir_lang.psi.qualification.Qualified;

/**
 * <expression> dotInfixOperator relativeIdentifier !CALL
 */
public interface QualifiedNoArgumentsCall extends Call, Qualified, Quotable {
}
