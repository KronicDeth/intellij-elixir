package org.elixir_lang.beam.decompiler;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Default extends MacroNameArity {
    /*
     * CONSTANTS
     */

    public static final MacroNameArity INSTANCE = new Default();


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
        appendMacro(decompiled, macroNameArity);

        String[] parameters = parameters(macroNameArity);
        appendSignature(decompiled, macroNameArity, macroNameArity.name, parameters);

        appendBody(decompiled);
    }

    void appendMacro(@NotNull StringBuilder decompiled, @NotNull org.elixir_lang.beam.MacroNameArity macroNameArity) {
        decompiled
                .append("  ")
                .append(macroNameArity.macro)
                .append(" ");
    }

    protected String[] parameters(@NotNull org.elixir_lang.beam.MacroNameArity macroNameArity) {
        int arity = macroNameArity.arity;
        String[] arguments = new String[arity];

        for (int i = 0; i < arity; i++) {
            arguments[i] = "p" + i;
        }

        return arguments;
    }

    @Override
    public void appendSignature(@NotNull StringBuilder decompiled,
                                @NotNull org.elixir_lang.beam.MacroNameArity macroNameArity,
                                @NotNull String name,
                                @NotNull String[] parameters) {
        appendName(decompiled, macroNameArity.name);

        decompiled.append('(');

        for (int i = 0; i < parameters.length; i++) {
            if (i != 0) {
                decompiled.append(", ");
            }

            decompiled.append(parameters[i]);
        }

        decompiled.append(')');
    }

    @Override
    public void appendName(@NotNull StringBuilder decompiled, @NotNull String name) {
        decompiled.append(name);
    }

}
