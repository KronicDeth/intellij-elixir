package org.elixir_lang.psi;

import org.elixir_lang.psi.call.StubBased;
import org.elixir_lang.psi.call.arguments.star.NoParenthesesOneArgument;
import org.elixir_lang.psi.call.qualification.Qualified;

/**
 * <expression> dotInfixOperator relativeIdentifier noParenthesesOneArgument
 */
public interface QualifiedNoParenthesesCall<Stub extends org.elixir_lang.psi.stub.call.Stub>
        extends NoParenthesesOneArgument, Qualified, Quotable, StubBased<Stub> {
}
