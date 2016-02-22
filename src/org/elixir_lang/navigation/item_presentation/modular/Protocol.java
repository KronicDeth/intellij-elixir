package org.elixir_lang.navigation.item_presentation.modular;

import org.elixir_lang.icons.ElixirIcons;
import org.elixir_lang.psi.call.Call;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class Protocol extends Module {
    /*
     * Constructors
     */

    /**
     * @param location the parent name of the Module that scopes {@code call}; {@code null} when scope is {@code quote}.
     * @param call     a {@code Kernel.defprotocol/2} call nested in {@code parent}.
     */
    public Protocol(@Nullable String location, @NotNull Call call) {
        super(location, call);
    }

    /**
     * The protocol icon
     */
    @Override
    @NotNull
    public Icon getIcon(boolean unused) {
        return ElixirIcons.PROTOCOL;
    }
}
