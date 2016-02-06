package org.elixir_lang.structure_view.element;

import org.jetbrains.annotations.Nullable;

/**
 * The visibility (public or private) or the {@link CallDefinition} or {@link CallDefinitionClause}
 */
public interface Visible {
    enum Visibility {
        PUBLIC,
        PRIVATE
    }

    /**
     * The visibility of the element.
     *
     * @return {@link Visibility.PUBLIC} for public call definitions ({@code def} and {@code defmacro});
     *   {@link Visibility.PRIVATE} for private call definitions ({@code defp} and {@code defmacrop}); {@code null} for
     *   a mix of visibilities, such as when a call definition has a mix of call definition clause visibilities, which
     *   is invalid code, but can occur temporarily while code is being edited.
     */
    @Nullable
    Visibility visibility();
}
