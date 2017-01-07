package org.elixir_lang.beam.decompiler;

import gnu.trove.THashSet;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class InfixOperator extends MacroNameArity {
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

    /**
     * Append the decompiled source for {@code macroNameArity} to {@code decompiled}.
     *
     * @param decompiled     the decompiled source so far
     */
    @Override
    public void append(@NotNull StringBuilder decompiled, @NotNull org.elixir_lang.beam.MacroNameArity macroNameArity) {
        decompiled
                .append("  ")
                .append(macroNameArity.macro)
                .append(" left ")
                .append(macroNameArity.name)
                .append(" right");
        appendBody(decompiled);
    }
}
