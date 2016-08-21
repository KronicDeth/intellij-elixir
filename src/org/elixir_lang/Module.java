package org.elixir_lang;

import com.intellij.openapi.util.text.StringUtil;
import org.apache.commons.lang.ArrayUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;

public class Module {
    /*
     * Constants
     */

    private static final String SEPARATOR = ".";

    /*
     * Public Static Methods
     */

    /**
     * Emulates Module.concat/1
     * @return
     */
    @Contract(pure = true)
    @NotNull
    public static String concat(@NotNull String... aliases) {
        return StringUtil.join(aliases, SEPARATOR);
    }

    @Contract(pure = true)
    @NotNull
    public static String concat(@NotNull Collection<String> aliases) {
        return StringUtil.join(aliases, SEPARATOR);
    }

    /**
     * Emulates Module.split/1
     */
    @Contract(pure = true)
    @NotNull
    public static java.util.List<String> split(@NotNull String name) {
        return StringUtil.split(name, SEPARATOR);
    }

}
