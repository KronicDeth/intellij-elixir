package org.elixir_lang.structure_view.element.modular;

import com.intellij.navigation.ItemPresentation;
import org.jetbrains.annotations.NotNull;

public class Use implements Modular {
    /*
     * Fields
     */

    @NotNull
    private final org.elixir_lang.structure_view.element.Use use;

    /*
     * Constructors
     */

    public Use(@NotNull org.elixir_lang.structure_view.element.Use use) {
        this.use = use;
    }

    /*
     * Instance Methods
     */

    @NotNull
    @Override
    public ItemPresentation getPresentation() {
        return new org.elixir_lang.navigation.item_presentation.modular.Use(use);
    }
}
