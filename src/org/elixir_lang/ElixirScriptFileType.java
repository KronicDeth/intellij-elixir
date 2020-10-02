package org.elixir_lang;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class ElixirScriptFileType extends ElixirFileType {
    public static final ElixirScriptFileType INSTANCE = new ElixirScriptFileType();

    @NotNull
    @Override
    public String getName() {
        return "Elixir Script";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Elixir Script file";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "exs";
    }
}
