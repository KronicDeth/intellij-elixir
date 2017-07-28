package org.elixir_lang.beam.decompiler;

import gnu.trove.THashSet;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

import static org.elixir_lang.beam.decompiler.Default.appendParameters;

public class Unquoted extends MacroNameArity {
    /*
     * CONSTANTS
     */

    private static final Set<String> SPECIAL_FORM_NAME_SET;
    public static final MacroNameArity INSTANCE = new Unquoted();

    static {
        SPECIAL_FORM_NAME_SET = new THashSet<String>();
        SPECIAL_FORM_NAME_SET.add("%");
        SPECIAL_FORM_NAME_SET.add("%{}");
        SPECIAL_FORM_NAME_SET.add("&");
        SPECIAL_FORM_NAME_SET.add(".");
        SPECIAL_FORM_NAME_SET.add("<<>>");
        SPECIAL_FORM_NAME_SET.add("do");
        SPECIAL_FORM_NAME_SET.add("fn");
        SPECIAL_FORM_NAME_SET.add("unquote");
        SPECIAL_FORM_NAME_SET.add("unquote_splicing");
        SPECIAL_FORM_NAME_SET.add("{}");
    }

    /*
     * Static Methods
     */

    /**
     * @param name {@link org.elixir_lang.beam.MacroNameArity#name}
     */
    private static boolean isSpecialForm(@NotNull String name) {
        return SPECIAL_FORM_NAME_SET.contains(name);
    }

    private static boolean wouldParseAsAlias(@NotNull String name) {
        return Character.isUpperCase(name.codePointAt(0));
    }

    /*
     * Instance Methods
     */

    /**
     * Wehther the decompiler accepts the {@code macroNameArity}.
     *
     * @return {@code true} if {@link #append(StringBuilder, org.elixir_lang.beam.MacroNameArity)} should be called with
     *   {@code macroNameArity}.
     */
    @Override
    public boolean accept(@NotNull org.elixir_lang.beam.MacroNameArity macroNameArity) {
        String name = macroNameArity.name;

        return wouldParseAsAlias(name) || isSpecialForm(name);
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
                .append(" unquote(:")
                .append(macroNameArity.name)
                .append(")");
        appendParameters(decompiled, macroNameArity);
        appendBody(decompiled);
    }
}
