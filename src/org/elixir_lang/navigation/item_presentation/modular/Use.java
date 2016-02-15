package org.elixir_lang.navigation.item_presentation.modular;

import com.intellij.navigation.ItemPresentation;
import org.elixir_lang.navigation.item_presentation.Parent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class Use implements ItemPresentation, Parent {
    /*
     * Fields
     */

    private final org.elixir_lang.structure_view.element.Use use;

    /*
     * Constructors
     */

    public Use(@NotNull org.elixir_lang.structure_view.element.Use use) {
        this.use = use;
    }


    /**
     * Returns the name of the object to be presented in most renderers across the program.
     *
     * @return the object name.
     */
    @NotNull
    @Override
    public String getPresentableText() {
        return use.call().getText();
    }

    /**
     * Combines {@link #getLocationString()} with {@link #getPresentableText()} for when this is the parent of
     * an {@link ItemPresentation} and needs to act as the
     * {@link ItemPresentation#getLocationString()}.
     *
     * @return {@link #getLocationString()} + "." + {@link #getPresentableText()} if {@link #getLocationString()} is not
     * {@code null}; otherwise, {@link #getPresentableText()}.
     */
    @NotNull
    @Override
    public String getLocatedPresentableText() {
        return getPresentableText();
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
        return null;
    }

    /**
     * Returns the icon representing the object.
     *
     * @param unused Used to mean if open/close icons for tree renderer. No longer in use. The parameter is only there for API compatibility reason.
     */
    @Nullable
    @Override
    public Icon getIcon(boolean unused) {
        return null;
    }
}
