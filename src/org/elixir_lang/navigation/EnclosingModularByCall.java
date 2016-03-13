package org.elixir_lang.navigation;

import org.elixir_lang.psi.call.Call;
import org.elixir_lang.structure_view.element.CallDefinitionClause;
import org.elixir_lang.structure_view.element.modular.Modular;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

/**
 * Keeps track of the enclosing {@link org.elixir_lang.structure_view.element.modular.Modular} for a
 * {@link org.elixir_lang.psi.call.Call}, so that looking up the
 * {@link org.elixir_lang.structure_view.element.CallDefinition} for a
 * {@link org.elixir_lang.structure_view.element.CallDefinitionClause} works correctly in {@link GotoSymbolContributor}
 */
public class EnclosingModularByCall extends HashMap<Call, Modular> {
    /**
     * Generates a {@link Modular} for the given {@code call} if it does not exist.
     *
     * @param call
     * @return {@code null} if {@code call} is top-level and has no enclosing modular.
     */
    @Nullable
    public Modular putNew(@NotNull Call call) {
        Modular modular;

        if (containsKey(call)) {
            modular = get(call);
        } else {
            modular = CallDefinitionClause.enclosingModular(call);
            put(call, modular);
        }

        return modular;
    }
}
