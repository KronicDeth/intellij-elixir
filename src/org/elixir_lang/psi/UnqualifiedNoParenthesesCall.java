package org.elixir_lang.psi;

import org.elixir_lang.psi.call.Call;
import org.elixir_lang.psi.call.MaybeExported;
import org.elixir_lang.psi.call.StubBased;
import org.elixir_lang.psi.call.arguments.star.NoParenthesesOneArgument;
import org.elixir_lang.psi.qualification.Unqualified;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * IDENTIFIER noParenthesesOneArgument
 */
public interface UnqualifiedNoParenthesesCall<Stub extends org.elixir_lang.psi.stub.call.Stub>
        extends Call, MaybeExported, NoParenthesesOneArgument, Quotable, StubBased<Stub>, Unqualified {
    @Contract(pure = true)
    @NotNull
    @Override
    String functionName();
}
