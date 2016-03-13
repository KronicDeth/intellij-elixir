package org.elixir_lang.psi;

import org.elixir_lang.psi.call.StubBased;
import org.elixir_lang.psi.call.arguments.None;
import org.elixir_lang.psi.call.qualification.Qualified;

/**
 * <expression> dotInfixOperator relativeIdentifier !CALL
 */
public interface QualifiedNoArgumentsCall<Stub extends org.elixir_lang.psi.stub.call.Stub>
        extends None, Qualified, Quotable, StubBased<Stub> {
}
