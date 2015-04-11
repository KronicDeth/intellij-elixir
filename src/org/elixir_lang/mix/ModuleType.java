package org.elixir_lang.mix;

import com.intellij.openapi.module.ModuleTypeManager;
import org.elixir_lang.ElixirIcon;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class ModuleType extends com.intellij.openapi.module.ModuleType {
    public static final String ID = "ELIXIR_MIX_PROJECT";

    @NotNull
    public static ModuleType getInstance() {
        return (ModuleType) ModuleTypeManager.getInstance().findByID(ID);
    }

    public ModuleType() {
        super(ID);
    }

    @NotNull
    @Override
    public ModuleBuilder createModuleBuilder() {
        return new ModuleBuilder();
    }

    @NotNull
    @Override
    public String getName() {
        return "Elixir Mix Project";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Elixir project created with <code>mix new</code>.";
    }

    @Override
    public Icon getBigIcon() {
        return ElixirIcon.FILE;
    }

    @Override
    public Icon getNodeIcon(@Deprecated boolean isOpened) {
        return ElixirIcon.FILE;
    }
}
