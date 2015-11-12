package org.elixir_lang.psi;

import org.elixir_lang.psi.qualification.Unqualified;

/**
 * IDENTIFIER CALL bracketArguments
 */
public interface UnqualifiedBracketOperation extends Quotable, Unqualified {
    Quotable getBracketArguments();
}
