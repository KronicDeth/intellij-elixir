package org.elixir_lang.navigation.item_presentation;

import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import org.elixir_lang.icons.ElixirIcons;
import org.elixir_lang.psi.call.Call;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class Module implements ItemPresentation, Parent {
    /*
     * Fields
     */

    @NotNull
    private final Call call;
    @Nullable
    private final Module parent;

    /*
     * Constructors
     */

    /**
     *
     * @param call a top-level `Kernel.defmodule/2` call
     */
    public Module(@NotNull final Call call) {
        this(null, call);
    }

    /**
     *
     * @param parent the parent {@link Module} that scopes {@code call}.
     * @param call a {@code Kernel.defmodule/2} call nested in {@code parent}.
     */
    public Module(@Nullable final Module parent, @NotNull final Call call) {
        this.call = call;
        this.parent = parent;
    }

    /*
     * Public Instance Methods
     */

    /**
     * Returns the name of the object to be presented in most renderers across the program.
     *
     * @return the function name.
     */
    @Override
    @NotNull
    public String getPresentableText() {
        PsiElement[] primaryArguments = call.primaryArguments();

        assert primaryArguments.length > 0;

        return primaryArguments[0].getText();
    }

    /**
     * Combines {@link #getLocationString()} with {@link #getPresentableText()} for when this Module is the parent of
     * another Module and needs to act as the location of the child Module.
     *
     * @return {@link #getLocationString()} + "." + {@link #getPresentableText()} if {@link #getLocationString()} is not
     *   {@code null}; otherwise, {@link #getPresentableText()}.
     */
    @Override
    @NotNull
    public String getLocatedPresentableText() {
        String locatedPresentableText;
        String locationString = getLocationString();

        if (locationString != null) {
            locatedPresentableText = locationString + "." + getPresentableText();
        } else {
            locatedPresentableText = getPresentableText();
        }

        return locatedPresentableText;
    }

    /**
     * Returns the location of the object (for example, the package of a class). The location
     * string is used by some renderers and usually displayed as grayed text next to the item name.
     *
     * @return the location description, or null if none is applicable.
     */
    @Nullable
    @Override
    public String getLocationString() {
        String locationString = null;

        if (parent != null) {
            locationString = parent.getLocatedPresentableText();
        }

        return locationString;
    }

    /**
     * The module icon.
     *
     * @param unused Used to mean if open/close icons for tree renderer. No longer in use. The parameter is only there for API compatibility reason.
     */
    @Override
    @NotNull
    public Icon getIcon(boolean unused) {
        return ElixirIcons.MODULE;
    }
}
