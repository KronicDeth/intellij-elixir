package org.elixir_lang.structure_view.element.modular;

import com.intellij.navigation.ItemPresentation;
import org.elixir_lang.structure_view.element.call.definition.Clause;
import org.jetbrains.annotations.NotNull;

/**
 * The anonymous module that is implied by a quote block that has been injected into a scope yet
 */
public class Quote implements Modular {
    @NotNull
    public final Clause callDefinitionClause;

    /*
     * Constructors
     */

    public Quote(@NotNull Clause callDefinitionClause) {
        this.callDefinitionClause = callDefinitionClause;
    }

    /*
     * Instance Methods
     */

    @NotNull
    @Override
    public ItemPresentation getPresentation() {
        return new org.elixir_lang.navigation.item_presentation.modular.Quote();
    }
}
