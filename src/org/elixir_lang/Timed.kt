package org.elixir_lang

import org.elixir_lang.semantic.call.definition.clause.Time

interface Timed {
    /**
     * When the defined call is usable
     *
     * @return [Time.COMPILE] for compile time (`defmacro`, `defmacrop`);
     * [Time.RUN] for run time `def`, `defp`)
     */
    val time: Time
}
