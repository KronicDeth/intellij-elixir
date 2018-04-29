package org.elixir_lang.navigation.item_presentation;

import org.elixir_lang.structure_view.element.CallDefinition;
import org.elixir_lang.structure_view.element.Timed;
import org.elixir_lang.structure_view.element.modular.Modular;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class CallDefinitionByTuple extends HashMap<CallDefinition.Tuple, CallDefinition> {

    /**
     * Generates a {@link CallDefinition} for the given
     * {@link org.elixir_lang.structure_view.element.CallDefinition.Tuple} if it does not exist.
     */
    @NotNull
    public CallDefinition putNew(@NotNull CallDefinition.Tuple tuple) {
        CallDefinition callDefinition = get(tuple);

        if (callDefinition == null) {
            callDefinition = new CallDefinition(tuple.getModular(), tuple.getTime(), tuple.getName(), tuple.getArity());
            put(tuple, callDefinition);
        }

        return callDefinition;
    }
}
