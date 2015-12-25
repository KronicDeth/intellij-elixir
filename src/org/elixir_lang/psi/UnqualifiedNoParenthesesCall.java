package org.elixir_lang.psi;

import org.elixir_lang.psi.call.Call;
import org.elixir_lang.psi.call.arguments.NoParentheses;
import org.elixir_lang.psi.call.arguments.NoParenthesesOneArgument;
import org.elixir_lang.psi.qualification.Unqualified;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * IDENTIFIER noParenthesesOneArgument
 */
public interface UnqualifiedNoParenthesesCall extends Call, NoParenthesesOneArgument, Quotable, Unqualified {
    @Contract(pure = true)
    @NotNull
    @Override
    String functionName();
}
