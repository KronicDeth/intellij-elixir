package org.elixir_lang.psi;

import org.elixir_lang.psi.call.arguments.None;
import org.elixir_lang.psi.call.qualification.Qualified;

/**
 * <expression> dotInfixOperator relativeIdentifier !CALL
 */
public interface QualifiedNoArgumentsCall extends None, Qualified, Quotable {
}
