package org.elixir_lang.psi;

import org.elixir_lang.psi.call.Call;
import org.elixir_lang.psi.call.StubBased;
import org.elixir_lang.psi.call.arguments.star.NoParenthesesOneArgument;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * atPrefixOperator IDENTIFIER CALL bracketArguments
 */
public interface AtUnqualifiedNoParenthesesCall<Stub extends org.elixir_lang.psi.stub.call.Stub>
        extends Call, NoParenthesesOneArgument, Quotable, StubBased<Stub> {
    @Contract(pure = true)
    @NotNull
    ElixirAtIdentifier getAtIdentifier();
}
