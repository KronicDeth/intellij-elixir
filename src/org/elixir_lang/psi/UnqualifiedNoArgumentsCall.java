package org.elixir_lang.psi;

import org.elixir_lang.psi.call.StubBased;
import org.elixir_lang.psi.call.arguments.None;
import org.elixir_lang.psi.qualification.Unqualified;

/**
 * IDENTIFIER !KEYWORD_PAIR_COLON doBlock?
 */
public interface UnqualifiedNoArgumentsCall<Stub extends org.elixir_lang.psi.stub.call.Stub>
        extends None, Quotable, StubBased<Stub>, Unqualified {
}
