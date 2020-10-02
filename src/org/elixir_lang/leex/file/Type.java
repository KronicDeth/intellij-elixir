package org.elixir_lang.leex.file;

import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;

public class Type extends org.elixir_lang.eex.file.Type {
    public static final LanguageFileType INSTANCE = new Type();

    @NotNull
    @Override
    public String getName() {
        return "Live Embedded Elixir";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Live Embedded Elixir file";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "leex";
    }
}
