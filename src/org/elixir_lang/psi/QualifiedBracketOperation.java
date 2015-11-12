package org.elixir_lang.psi;

import org.elixir_lang.psi.qualification.Qualified;

/**
 * <expression> dotInfixOperator relativeIdentifier CALL bracketArguments
 */
public interface QualifiedBracketOperation extends Qualified, Quotable {
    Quotable getBracketArguments();
}
