package org.elixir_lang;

import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Created by luke.imhoff on 7/27/14.
 */
public class ElixirFileType extends LanguageFileType {
    public static final ElixirFileType INSTANCE = new ElixirFileType();

    protected ElixirFileType() {
        super(ElixirLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "Elixir";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Elixir language file";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "ex";
    }

    @NotNull
    @Override
    public Icon getIcon() {
        return Icons.FILE;
    }
}
