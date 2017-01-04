package org.elixir_lang.beam;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.elixir_lang.psi.call.name.Function.DEF;
import static org.elixir_lang.psi.call.name.Function.DEFMACRO;

/**
 * The macro ({@code def} or {@code defmacro}), name of the call definition and arity to define an export
 */
public class MacroNameArity {
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
     *
     * Only {@code null} if {@code exportArity} is {@code null}, which would indicate a malformed {@code .beam}.
     */
    @Nullable
    public final Integer arity;

    /*
     * Constructors
     */

    public MacroNameArity(@NotNull String exportName, @Nullable Integer exportArity) {
        if (exportName.startsWith(MACRO_EXPORT_PREFIX)) {
            macro = DEFMACRO;
            name = exportName.substring(MACRO_EXPORT_PREFIX.length());

            if (exportArity != null) {
                arity = exportArity - 1;
            } else {
                arity = null;
            }
        } else {
            macro = DEF;
            name = exportName;
            arity = exportArity;
        }
    }
}
