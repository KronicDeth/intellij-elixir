package org.elixir_lang.psi;

import org.elixir_lang.psi.call.Call;
import org.elixir_lang.psi.call.StubBased;
import org.elixir_lang.psi.call.arguments.star.Parentheses;
import org.elixir_lang.psi.qualification.Unqualified;

/**
 * {@code IDENTIFIER matchedParenthesesArguments}
 */
public interface UnqualifiedParenthesesCall<Stub extends org.elixir_lang.psi.stub.call.Stub>
        extends Call, Parentheses, Quotable, StubBased<Stub>, Unqualified {
}
