package org.elixir_lang.psi;

import com.intellij.psi.stubs.StubBase;
import org.elixir_lang.psi.call.StubBased;
import org.elixir_lang.psi.call.arguments.star.Parentheses;
import org.elixir_lang.psi.call.qualification.Qualified;

/**
 * <expression> dotInfixOperator relativeIdentifier matchedParenthesesArguments
 */
public interface QualifiedParenthesesCall<Stub extends org.elixir_lang.psi.stub.call.Stub>
        extends Parentheses, Qualified, Quotable, StubBased<Stub> {
}
