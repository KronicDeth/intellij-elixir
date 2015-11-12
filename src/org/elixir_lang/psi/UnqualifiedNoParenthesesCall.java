package org.elixir_lang.psi;

import org.elixir_lang.psi.call.Call;
import org.elixir_lang.psi.qualification.Unqualified;
import org.jetbrains.annotations.NotNull;

/**
 * IDENTIFIER noParenthesesOneArgument
 */
public interface UnqualifiedNoParenthesesCall extends Call, Quotable, Unqualified {
    @NotNull
    String functionName();

    ElixirNoParenthesesOneArgument getNoParenthesesOneArgument();
}
