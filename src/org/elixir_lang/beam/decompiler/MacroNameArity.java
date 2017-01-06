package org.elixir_lang.beam.decompiler;

import org.jetbrains.annotations.NotNull;

/**
 * Decompiles a {@link org.elixir_lang.beam.MacroNameArity}
 */
public abstract class MacroNameArity {
    /*
     * Static Methods
     */

    static void appendBody(@NotNull StringBuilder decompiled) {
        decompiled
                .append(" do\n")
                .append("    # body not decompiled\n")
                .append("  end\n");
    }

    /*
     * Instance Methods
     */


    /**
     * Whether the decompiler accepts the {@code macroNameArity}
     *
     * @return {@code true} if {@link #append(StringBuilder, org.elixir_lang.beam.MacroNameArity)} should be called with
     *   {@code macroNameArity}.
     */
    public abstract boolean accept(@NotNull org.elixir_lang.beam.MacroNameArity macroNameArity);

    /**
     * Append the decompiled source for {@code macroNameArity} to {@code decompiled}.
     *
     * @param decompiled the decompiled source so far
     */
    public abstract void append(@NotNull StringBuilder decompiled,
                                @NotNull org.elixir_lang.beam.MacroNameArity macroNameArity);
}
