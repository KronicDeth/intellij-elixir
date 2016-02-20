package org.elixir_lang.structure_view.element.call_definition_by_name_arity;

import com.intellij.openapi.util.Pair;
import org.elixir_lang.structure_view.element.CallDefinition;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface CallDefinitionbyNameArity extends Map<Pair<String, Integer>, CallDefinition> {
    /**
     * Generates a {@link CallDefinition} for the given {@code nameArity} if it does not exist.
     *
     * The {@link CallDefinition} is
     * @param nameArity
     * @return pre-existing {@link CallDefinition} or new {@link CallDefinition} add to the {@code List<TreeElement>}
     */
    @NotNull
    CallDefinition putNew(@NotNull Pair<String, Integer> nameArity);
}
