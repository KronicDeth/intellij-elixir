package org.elixir_lang.psi;

import org.elixir_lang.psi.qualification.Qualified;

/**
 * <expression> dotInfixOperator relativeIdentifier noParenthesesOneArgument
 */
public interface QualifiedNoParenthesesCall extends Qualified, Quotable {
    ElixirDoBlock getDoBlock();

    ElixirNoParenthesesOneArgument getNoParenthesesOneArgument();
}
