package org.elixir_lang.psi;

import org.jetbrains.annotations.NotNull;

/**
 * <expression> dotInfixOperator multipleAliases
 */
public interface QualifiedMultipleAliases extends Quotable {
    @NotNull
    ElixirMultipleAliases getMultipleAliases();
}
