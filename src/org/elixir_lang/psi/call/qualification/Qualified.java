package org.elixir_lang.psi.call.qualification;

import org.elixir_lang.psi.call.Call;
import org.jetbrains.annotations.NotNull;

public interface Qualified extends Call, org.elixir_lang.psi.qualification.Qualified {
    /**
     *
     * @return name of the qualifying module as given in the source
     */
    @NotNull
    @Override
    String moduleName();

    /**
     * @return name of the qualifying module after taking into account any aliases
     */
    @NotNull
    @Override
    String resolvedModuleName();
}
