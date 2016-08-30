package org.elixir_lang.psi.scope.module;

import com.intellij.openapi.util.Condition;
import org.jetbrains.annotations.NotNull;

class Longer implements Condition<String> {
    /*
     * Fields
     */

    private final int length;

    /*
     * Constructors
     */

    Longer(int length) {
        this.length = length;
    }

    /*
     * Public Instance Methods
     */

    /**
     * @param string
     * @return {@code true} if {@code string} is greather than {@link #length}
     */
    @Override
    public boolean value(@NotNull String string) {
        return string.length() > length;
    }
}
