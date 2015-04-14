package org.elixir_lang.mix;

import org.jetbrains.annotations.NotNull;

import java.io.File;

public class DetectedSourceRoot extends com.intellij.ide.util.projectWizard.importSources.DetectedSourceRoot {
    public DetectedSourceRoot(File directory) {
        super(directory, null);
    }

    @NotNull
    @Override
    public String getRootTypeName() {
        return "Elixir";
    }
}
