package org.elixir_lang.psi;

import org.elixir_lang.psi.call.Call;
import org.elixir_lang.psi.call.arguments.NoParenthesesOneArgument;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * atPrefixOperator IDENTIFIER CALL bracketArguments
 */
public interface AtUnqualifiedNoParenthesesCall extends Call, ModuleAttributeNameable, NamedElement,
        NoParenthesesOneArgument, Quotable {
    @Contract(pure = true)
    @NotNull
    ElixirAtIdentifier getAtIdentifier();
}
