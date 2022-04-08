package org.elixir_lang.structure_view.element;

import org.elixir_lang.semantic.call.definition.clause.Visibility;
import org.elixir_lang.structure_view.element.call.Definition;
import org.elixir_lang.structure_view.element.call.definition.Clause;
import org.jetbrains.annotations.Nullable;

/**
 * The visibility (public or private) or the {@link Definition} or {@link Clause}
 */
public interface Visible {

    /**
     * The visibility of the element.
     *
     * @return {@link Visibility#PUBLIC} for public call definitions ({@code def} and {@code defmacro});
     * {@link Visibility#PRIVATE} for private call definitions ({@code defp} and {@code defmacrop}); {@code null} for
     * a mix of visibilities, such as when a call definition has a mix of call definition clause visibilities, which
     * is invalid code, but can occur temporarily while code is being edited.
     */
    @Nullable
    Visibility visibility();
}
