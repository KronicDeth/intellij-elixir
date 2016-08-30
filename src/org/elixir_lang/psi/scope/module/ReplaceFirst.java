package org.elixir_lang.psi.scope.module;

import com.intellij.util.Function;
import org.jetbrains.annotations.NotNull;

public class ReplaceFirst implements Function<String, String> {
    /*
     * Fields
     */

    @NotNull
    private final String original;
    @NotNull
    private final String replacement;

    /*
     * Constructor
     */

    ReplaceFirst(@NotNull String original, @NotNull String replacement) {
        this.original = original;
        this.replacement = replacement;
    }

    /*
     * Public Instance Methods
     */

    @Override
    public String fun(String originalPrefixed) {
        return originalPrefixed.replaceFirst(original, replacement);
    }
}
