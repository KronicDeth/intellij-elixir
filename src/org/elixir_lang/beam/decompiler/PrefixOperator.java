package org.elixir_lang.beam.decompiler;

import gnu.trove.THashSet;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class PrefixOperator extends MacroNameArity {
    /*
     * CONSTANTS
     */

    private static final Set<String> PREFIX_OPERATOR_SET;
    public static final MacroNameArity INSTANCE = new PrefixOperator();

    static {
        PREFIX_OPERATOR_SET = new THashSet<String>();
        PREFIX_OPERATOR_SET.add("+");
        PREFIX_OPERATOR_SET.add("-");
    }

    /*
     * Static Methods
     */

    /**
     * @parma name {@link org.elixir_lang.beam.MacroNameArity#name}
     */
    private static boolean isPrefixOperator(@NotNull String name) {
        return PREFIX_OPERATOR_SET.contains(name);
    }

    /*
     * Instance Methods
     */

    /**
     * Whether the decompiler accepts the {@code macroNameArity}
     *
     * @return {@code true} if {@link #append(StringBuilder, org.elixir_lang.beam.MacroNameArity)} should be called with
     * {@code macroNameArity}.
     */
    @Override
    public boolean accept(@NotNull org.elixir_lang.beam.MacroNameArity macroNameArity) {
        Integer arity = macroNameArity.arity;

        return arity != null && arity == 1 && isPrefixOperator(macroNameArity.name);
    }

    /**
     * Append the decompiled source for {@code macroNameArity} to {@code decompiled}.
     *
     * @param decompiled     the decompiled source so far
     * @param macroNameArity
     */
    @Override
    public void append(@NotNull StringBuilder decompiled, @NotNull org.elixir_lang.beam.MacroNameArity macroNameArity) {
        decompiled
                .append("  ")
                .append(macroNameArity.macro)
                .append(" (");

        appendName(decompiled, macroNameArity.name);
        decompiled.append("value)");
        appendBody(decompiled);
    }

    @Override
    public void appendName(@NotNull StringBuilder decompiled, @NotNull String name) {
        decompiled.append(name);
    }
}
