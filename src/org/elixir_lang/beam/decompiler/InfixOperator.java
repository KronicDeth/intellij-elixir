package org.elixir_lang.beam.decompiler;

import gnu.trove.THashSet;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class InfixOperator extends Default {
    /*
     * CONSTANTS
     */

    private static final Set<String> INFIX_OPERATOR_SET;
    public static final MacroNameArity INSTANCE = new InfixOperator();

    static {
        INFIX_OPERATOR_SET = new THashSet<String>();
        INFIX_OPERATOR_SET.add("!=");
        INFIX_OPERATOR_SET.add("!==");
        INFIX_OPERATOR_SET.add("");
        INFIX_OPERATOR_SET.add("&&");
        INFIX_OPERATOR_SET.add("&&&");
        INFIX_OPERATOR_SET.add("*");
        INFIX_OPERATOR_SET.add("+");
        INFIX_OPERATOR_SET.add("++");
        INFIX_OPERATOR_SET.add("-");
        INFIX_OPERATOR_SET.add("--");
        INFIX_OPERATOR_SET.add("--");
        INFIX_OPERATOR_SET.add("->");
        INFIX_OPERATOR_SET.add("..");
        INFIX_OPERATOR_SET.add("/");
        INFIX_OPERATOR_SET.add("::");
        INFIX_OPERATOR_SET.add("<");
        INFIX_OPERATOR_SET.add("<-");
        INFIX_OPERATOR_SET.add("<<<");
        INFIX_OPERATOR_SET.add("<<~");
        INFIX_OPERATOR_SET.add("<=");
        INFIX_OPERATOR_SET.add("<>");
        INFIX_OPERATOR_SET.add("<|>");
        INFIX_OPERATOR_SET.add("<~");
        INFIX_OPERATOR_SET.add("<~>");
        INFIX_OPERATOR_SET.add("=");
        INFIX_OPERATOR_SET.add("==");
        INFIX_OPERATOR_SET.add("===");
        INFIX_OPERATOR_SET.add("=>");
        INFIX_OPERATOR_SET.add("=~");
        INFIX_OPERATOR_SET.add(">");
        INFIX_OPERATOR_SET.add(">=");
        INFIX_OPERATOR_SET.add(">>>");
        INFIX_OPERATOR_SET.add("\\\\");
        INFIX_OPERATOR_SET.add("^");
        INFIX_OPERATOR_SET.add("^^^");
        INFIX_OPERATOR_SET.add("and");
        INFIX_OPERATOR_SET.add("in");
        INFIX_OPERATOR_SET.add("or");
        INFIX_OPERATOR_SET.add("|>");
        INFIX_OPERATOR_SET.add("||");
        INFIX_OPERATOR_SET.add("|||");
        INFIX_OPERATOR_SET.add("~=");
        INFIX_OPERATOR_SET.add("~>");
        INFIX_OPERATOR_SET.add("~>>");
    }

    /*
     * Static Methods
     */

    /**
     * @param name {@link org.elixir_lang.beam.MacroNameArity#name}
     */
    private static boolean isInfixOperator(@NotNull String name) {
        return INFIX_OPERATOR_SET.contains(name);
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

        return arity != null && arity == 2 && isInfixOperator(macroNameArity.name);
    }

    @Override
    protected String[] parameters(@NotNull org.elixir_lang.beam.MacroNameArity macroNameArity) {
        return new String[]{"left", "right"};
    }

    @Override
    public void appendSignature(@NotNull StringBuilder decompiled,
                                @NotNull org.elixir_lang.beam.MacroNameArity macroNameArity,
                                @NotNull String name,
                                @NotNull String[] parameters) {
        decompiled.append(parameters[0]);
        appendName(decompiled, macroNameArity.name);
        decompiled.append(parameters[1]);
    }

    @Override
    public void appendName(@NotNull StringBuilder decompiled, @NotNull String name) {
        decompiled.append(' ').append(name).append(' ');
    }
}
