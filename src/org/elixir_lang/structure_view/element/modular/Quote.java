package org.elixir_lang.structure_view.element.modular;

import com.intellij.navigation.ItemPresentation;
import org.elixir_lang.structure_view.element.CallDefinitionClause;
import org.jetbrains.annotations.NotNull;

/**
 * The anonymous module that is implied by a quote block that has been injected into a scope yet
 */
public class Quote implements Modular {
    @NotNull
    private final CallDefinitionClause callDefinitionClause;

    /*
     * Constructors
     */

    public Quote(@NotNull CallDefinitionClause callDefinitionClause) {
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
