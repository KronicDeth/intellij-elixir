package org.elixir_lang.beam.decompiler;

import gnu.trove.THashSet;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class PrefixOperator extends Default {
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

    @Override
    protected String[] parameters(@NotNull org.elixir_lang.beam.MacroNameArity macroNameArity) {
        return new String[]{"value"};
    }

    @Override
    public void appendSignature(@NotNull StringBuilder decompiled,
                                @NotNull org.elixir_lang.beam.MacroNameArity macroNameArity,
                                @NotNull String name,
                                @NotNull String[] parameters) {
        decompiled.append('(');
        appendName(decompiled, macroNameArity.name);
        decompiled.append(parameters[0]).append(')');
    }
}
