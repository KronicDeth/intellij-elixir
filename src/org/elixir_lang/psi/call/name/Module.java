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
     * Strips {@code "Elixir."} from the module name if it is present
     *
     * @param maybeFullyQualified a fully qualified module name or a module name without the {@code "Elixir."} prefix
     */
    public static String stripElixirPrefix(@NotNull String maybeFullyQualified) {
        String stripped = maybeFullyQualified;

        if (maybeFullyQualified.startsWith(ELIXIR_PREFIX)) {
            stripped = maybeFullyQualified.substring(ELIXIR_PREFIX.length());
        }

        return stripped;
    }
}
