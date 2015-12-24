package org.elixir_lang.psi;

import org.elixir_lang.psi.call.Call;
import org.elixir_lang.psi.call.arguments.None;
import org.elixir_lang.psi.qualification.Unqualified;

/**
 * IDENTIFIER !KEYWORD_PAIR_COLON doBlock?
 */
public interface UnqualifiedNoArgumentsCall extends None, Call, Quotable, Unqualified {
}
