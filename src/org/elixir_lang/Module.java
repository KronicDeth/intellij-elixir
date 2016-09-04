package org.elixir_lang;

import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.text.StringUtil;
import org.apache.commons.lang.ArrayUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;

public class Module {
    public static class IsNestedUnder implements Condition<String> {
        /*
         * Fields
         */

        @NotNull
        private java.util.List<String> splitModuleName;

        /*
         * Constructors
         */

        public IsNestedUnder(@NotNull String moduleName) {
            this.splitModuleName = split(moduleName);
        }

        /*
         * Public Instance Methods
         */

        @Override
        public boolean value(@NotNull String maybeStartsWithModuleName) {
            java.util.List<String> splitMaybStartsIwthModuleName = split(maybeStartsWithModuleName);
            boolean isNestedUnder = true;

            if (splitMaybStartsIwthModuleName.size() > splitModuleName.size()) {
                for (int i = 0; i < splitModuleName.size(); i++) {
                    if (!splitMaybStartsIwthModuleName.get(i).equals(splitModuleName.get(i))) {
                        isNestedUnder = false;

                        break;
                    }
                }
            } else {
                isNestedUnder = false;
            }

            return isNestedUnder;
        }
    }

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
