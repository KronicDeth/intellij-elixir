package org.elixir_lang.structure_view.element;

import org.jetbrains.annotations.NotNull;

public interface Timed {
    /**
     * When the defined call is usable
     *
     * @return {@link Time#COMPILE} for compile time ({@code defmacro}, {@code defmacrop});
     *   {@link Time#RUN} for run time {@code def}, {@code defp})
     */
    @NotNull
    Time time();

    enum Time {
        COMPILE,
        RUN
    }
}
