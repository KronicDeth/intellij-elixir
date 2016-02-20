package org.elixir_lang.navigation.item_presentation;

import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import org.elixir_lang.icons.ElixirIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class Callback implements ItemPresentation {
    /*
     * Fields
     */

    @NotNull
    private final PsiElement[] arguments;
    @NotNull
    private final String location;

    /*
     * Constructors
     */

    public Callback(@NotNull String location, @NotNull PsiElement[] arguments) {
        assert arguments.length > 0;

        this.arguments = arguments;
        this.location = location;
    }

    /*
     * Instance Methods
     */

    /**
     * Returns the icon representing the object.
     *
     * @param unused Used to mean if open/close icons for tree renderer. No longer in use. The parameter is only there for API compatibility reason.
     */
    @Nullable
    @Override
    public Icon getIcon(boolean unused) {
        return ElixirIcons.CALLBACK;
    }

    /**
     * The module or anonymous module implied by {@code quote}.
     *
     *
     * @return the modular name
     */
    @Nullable
    @Override
    public String getLocationString() {
        return location;
    }

    /**
     * Returns the name of the object to be presented in most renderers across the program.
     *
     * @return the object name.
     */
    @Nullable
    @Override
    public String getPresentableText() {
        return arguments[0].getText();
    }

}
