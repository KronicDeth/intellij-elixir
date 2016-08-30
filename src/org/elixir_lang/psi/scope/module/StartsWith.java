package org.elixir_lang.psi.scope.module;

import com.intellij.openapi.util.Condition;
import org.jetbrains.annotations.NotNull;

class StartsWith implements Condition<String> {
    /*
     * Fields
     */

    @NotNull
    private final String prefix;

    /*
     * Constructors
     */

    StartsWith(@NotNull String prefix) {
        this.prefix = prefix;
    }


    /*
     * Public Instance Methods
     */

    /**
     * @param full a full String
     * @return {@code true} if {@code full} starts with {@link #prefix}
     */
    @Override
    public boolean value(@NotNull String full) {
        return full.startsWith(prefix);
    }
}
