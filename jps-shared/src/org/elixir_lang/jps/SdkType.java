package org.elixir_lang.jps;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class SdkType {
    @Nullable
    public static String exeFileToExePath(@NotNull File erlFile) {
        String erlExePath = null;

        if (erlFile.exists() && erlFile.canExecute()) {
            erlExePath = erlFile.getAbsolutePath();
        }

        return erlExePath;
    }
}
