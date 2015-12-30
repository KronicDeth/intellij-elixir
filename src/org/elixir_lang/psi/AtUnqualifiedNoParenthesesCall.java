package org.elixir_lang.psi;

import org.elixir_lang.psi.call.Call;
import org.elixir_lang.psi.call.arguments.NoParentheses;
import org.elixir_lang.psi.call.arguments.NoParenthesesOneArgument;

/**
 * atPrefixOperator IDENTIFIER CALL bracketArguments
 */
public interface AtUnqualifiedNoParenthesesCall extends Call, ModuleAttributeNameable, NoParenthesesOneArgument,
        Quotable {
    Quotable getAtPrefixOperator();
}
