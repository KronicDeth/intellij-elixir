package org.elixir_lang.navigation.item_presentation;

import com.intellij.navigation.ItemPresentation;
import org.elixir_lang.icons.ElixirIcons;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class Overridable implements ItemPresentation {
    /*
     * Fields
     */

    @Nullable
    private final String location;

    /*
     * Constructors
     */

    public Overridable(@Nullable String location) {
        this.location = location;
    }

    /*
     * Instance Method
     */

    /**
     * Returns the location of the object (for example, the package of a class). The location
     * string is used by some renderers and usually displayed as grayed text next to the item name.
     *
     * @return the location description, or null if none is applicable.
     */
    @Nullable
    @Override
    public String getLocationString() {
        return location;
    }

    /**
     * Returns the icon representing the object.
     *
     * @param unused Used to mean if open/close icons for tree renderer. No longer in use. The parameter is only there for API compatibility reason.
     */
    @Nullable
    @Override
    public Icon getIcon(boolean unused) {
        return ElixirIcons.OVERRIDABLE;
    }

    /**
     * Returns the name of the object to be presented in most renderers across the program.
     *
     * @return the object name.
     */
    @Nullable
    @Override
    public String getPresentableText() {
        return "defoverridable";
    }

}
