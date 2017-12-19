package org.elixir_lang;

import com.intellij.facet.FacetType;
import com.intellij.facet.FacetTypeId;
import com.intellij.openapi.module.Module;
import org.elixir_lang.facet.Configuration;
import org.jetbrains.annotations.NotNull;

public class Facet extends com.intellij.facet.Facet<Configuration> {
    public static final FacetTypeId<Facet> ID = new FacetTypeId<>("elixir");

    public Facet(@NotNull FacetType facetType,
                 @NotNull Module module,
                 @NotNull String name,
                 @NotNull Configuration configuration,
                 com.intellij.facet.Facet underlyingFacet) {
        super(facetType, module, name, configuration, underlyingFacet);
    }
}
