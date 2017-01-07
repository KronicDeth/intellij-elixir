package org.elixir_lang.beam;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.elixir_lang.psi.call.name.Function.DEF;
import static org.elixir_lang.psi.call.name.Function.DEFMACRO;

/**
 * The macro ({@code def} or {@code defmacro}), name of the call definition and arity to define an export
 */
public class MacroNameArity implements Comparable<MacroNameArity> {
    /*
     * CONSTANTS
     */

    private static final String MACRO_EXPORT_PREFIX = "MACRO-";

    /*
     * Fields
     */

    /**
     * {@code defmacro} if exportArity is prefixed with {@code MACRO-}; otherwise, {@code def}.
     */
    @NotNull
    public final String macro;

    /**
     * Elixir macros are defined as Erlang functions with names prefixed by {@code MACRO-}.  That prefix is stripped
     * from this name as it is the call definition name passed to {@code def} or {@code defmacro}.
     */
    @NotNull
    public final String name;

    /**
     * Elixir macros are defined as Erlang functions that take an extra Caller argument, so the exportArity is +1 for
     * macros compared to this arity, which is the number of parameters to the call definition head passed to
     * {@code defmacro}.
     */
    @NotNull
    public final Integer arity;

    /*
     * Constructors
     */

    public MacroNameArity(@NotNull String exportName, int exportArity) {
        if (exportName.startsWith(MACRO_EXPORT_PREFIX)) {
            macro = DEFMACRO;
            name = exportName.substring(MACRO_EXPORT_PREFIX.length());
            arity = exportArity - 1;
        } else {
            macro = DEF;
            name = exportName;
            arity = exportArity;
        }
    }

    /*
     * Instance Methods
     */

    @Override
    public int compareTo(@NotNull MacroNameArity other) {
        int comparison = compareMacroTo(other.macro);

        if (comparison == 0) {
            comparison = compareNameTo(other.name);

            if (comparison == 0) {
                comparison = compareArityTo(other.arity);
            }
        }

        return comparison;
    }

    @Contract(pure = true)
    private int compareArityTo(int otherArity) {
        return Double.compare(arity, otherArity);
    }

    @Contract(pure = true)
    private int compareMacroTo(@NotNull String otherMacro) {
        int comparison;

        if (macro.equals(DEFMACRO)) {
            if (otherMacro.equals(DEFMACRO)) {
                comparison = 0;
            } else if (otherMacro.equals(DEF)) {
                comparison = - 1;
            } else {
                throw new IllegalArgumentException("otherMacro must be \"" + DEFMACRO + "\" or \"" + DEF + "\"");
            }
        } else if (macro.equals(DEF)) {
            if (otherMacro.equals(DEFMACRO)) {
                comparison = 1;
            } else if (otherMacro.equals(DEF)) {
                comparison = 0;
            } else {
                throw new IllegalArgumentException("otherMacro must be \"" + DEFMACRO + "\" or \"" + DEF + "\"");
            }
        } else {
            throw new IllegalArgumentException("macro must be \"" + DEFMACRO + "\" or \"" + DEF + "\"");
        }

        return comparison;
    }

    @Contract(pure = true)
    private int compareNameTo(@NotNull String otherName) {
        return name.compareTo(otherName);
    }
}
