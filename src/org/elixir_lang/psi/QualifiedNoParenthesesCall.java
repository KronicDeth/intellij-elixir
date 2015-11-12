package org.elixir_lang.psi;

import org.elixir_lang.psi.call.qualification.Qualified;

/**
 * <expression> dotInfixOperator relativeIdentifier noParenthesesOneArgument
 */
public interface QualifiedNoParenthesesCall extends Qualified, Quotable {
    ElixirNoParenthesesOneArgument getNoParenthesesOneArgument();
}
