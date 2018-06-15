package org.elixir_lang.jps;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.AccessDeniedException;

public class SdkType {
    @NotNull
    public static String exeFileToExePath(@NotNull File file) throws FileNotFoundException, AccessDeniedException {
        String path;

        if (file.exists()) {
            if (file.canExecute()) {
                path = file.getAbsolutePath();
            } else {
                throw new AccessDeniedException(file.getAbsolutePath(), null, " is not executable");
            }
        } else {
            throw new FileNotFoundException(file.getAbsolutePath() + " does not exist");
        }

        return path;
    }
}
