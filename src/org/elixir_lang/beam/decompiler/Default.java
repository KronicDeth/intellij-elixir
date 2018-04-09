package org.elixir_lang.beam.decompiler;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Default extends MacroNameArity {
    /*
     * CONSTANTS
     */

    public static final MacroNameArity INSTANCE = new Default();

    /*
     * Static Methods
     */

    static void appendParameters(@NotNull StringBuilder decompiled,
                                 @NotNull org.elixir_lang.beam.MacroNameArity macroNameArity) {
        decompiled.append("(");

        @Nullable Integer arity = macroNameArity.arity;

        if (arity != null) {
            for (int i = 0; i < arity; i++) {
                if (i != 0) {
                    decompiled.append(", ");
                }

                decompiled.append("p").append(i);
            }
        }

        decompiled.append(")");
    }

    /**
     * Whether the decompiler accepts the {@code macroNameArity}
     *
     * @return {@code true}
     */
    @Override
    public boolean accept(@NotNull org.elixir_lang.beam.MacroNameArity macroNameArity) {
        return true;
    }

    /**
     * Append the decompiled source for {@code macroNameArity} to {@code decompiled}.
     *
     * @param decompiled the decompiled source so far
     */
    @Override
    public void append(@NotNull StringBuilder decompiled, @NotNull org.elixir_lang.beam.MacroNameArity macroNameArity) {
        decompiled
                .append("  ")
                .append(macroNameArity.macro)
                .append(" ");

        appendName(decompiled, macroNameArity.name);
        appendParameters(decompiled, macroNameArity);
        appendBody(decompiled);
    }

    @Override
    public void appendName(@NotNull StringBuilder decompiled, @NotNull String name) {
        decompiled.append(name);
    }
}
