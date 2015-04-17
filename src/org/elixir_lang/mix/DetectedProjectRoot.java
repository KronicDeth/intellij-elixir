package org.elixir_lang.mix;

import org.jetbrains.annotations.NotNull;

import java.io.File;

public class DetectedProjectRoot extends com.intellij.ide.util.projectWizard.importSources.DetectedProjectRoot {
    public DetectedProjectRoot(@NotNull File directory) {
        super(directory);
    }

    @NotNull
    @Override
    public String getRootTypeName() {
        return "Elixir";
    }
}
