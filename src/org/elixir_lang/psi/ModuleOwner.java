package org.elixir_lang.psi;

import org.elixir_lang.psi.call.CanonicallyNamed;
import org.jetbrains.annotations.NotNull;

public interface ModuleOwner {
    /**
     * @return modulars owned (declared) by this element.
     */
    @NotNull
    CanonicallyNamed[] modulars();
}
