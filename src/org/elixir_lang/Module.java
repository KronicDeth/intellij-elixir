package org.elixir_lang;

import com.intellij.openapi.util.text.StringUtil;
import org.apache.commons.lang.ArrayUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;

public class Module {
    /**
     * Emulates Module.concat/1
     * @return
     */
    @Contract(pure = true)
    @NotNull
    public static String concat(@NotNull String... aliases) {
        return StringUtil.join(aliases, ".");
    }

    @Contract(pure = true)
    @NotNull
    public static String concat(@NotNull Collection<String> aliases) {
        return StringUtil.join(aliases, ".");
    }

    public static String[] reverse(@NotNull String... forward) {
        String[] reversed = Arrays.copyOf(forward, 0);
        ArrayUtils.reverse(reversed);

        return reversed;
    }
}
