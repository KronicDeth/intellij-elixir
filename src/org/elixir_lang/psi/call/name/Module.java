package org.elixir_lang.psi.call.name;

import org.jetbrains.annotations.NotNull;

public class Module {
    /*
     *
     * CONSTANTS
     *
     */

    public static final String KERNEL = "Kernel";
    public static final String ELIXIR_PREFIX = "Elixir.";

    /*
     * Static Methods
     */


    /**
     * Converts an (potentially) unqualified module name like {@code "Kernel"} to the fully-qualified atom
     * {@code "Elixir.Kernel"}.
     *
     * @return {@code "Elixir.#{moduleName}" if {@code moduleName} does not start with {@code "Elixir"}; otherwise,
     *   {@code moduleName}.
     */
    @NotNull
    public static String prependElixirPrefix(@NotNull String moduleName) {
        String atomString = moduleName;

        if (!moduleName.startsWith("Elixir.")) {
            atomString = "Elixir." + moduleName;
        }

        return atomString;
    }

    /**
     * Strips {@code "Elixir."} from the module name if it is present
     *
     * @param maybeFullyQualified a fully qualified module name or a module name without the {@code "Elixir."} prefix
     */
    @NotNull
    public static String stripElixirPrefix(@NotNull String maybeFullyQualified) {
        String stripped = maybeFullyQualified;

        if (maybeFullyQualified.startsWith(ELIXIR_PREFIX)) {
            stripped = maybeFullyQualified.substring(ELIXIR_PREFIX.length());
        }

        return stripped;
    }
}
