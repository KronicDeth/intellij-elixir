package org.elixir_lang.navigation.item_presentation.modular;

import com.intellij.navigation.ItemPresentation;
import com.intellij.ui.RowIcon;
import com.intellij.util.PlatformIcons;
import org.elixir_lang.icons.ElixirIcons;
import org.elixir_lang.navigation.item_presentation.Parent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class Quote implements ItemPresentation, Parent {
    /**
     * Returns the icon representing the object.
     *
     * @param unused Used to mean if open/close icons for tree renderer. No longer in use. The parameter is only there for API compatibility reason.
     */
    @Nullable
    @Override
    public Icon getIcon(boolean unused) {
        RowIcon rowIcon = new RowIcon(2);

        rowIcon.setIcon(ElixirIcons.MODULE, 0);
        rowIcon.setIcon(PlatformIcons.ANONYMOUS_CLASS_ICON, 1);

        return rowIcon;
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
        return "?";
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
     * Returns the name of the object to be presented in most renderers across the program.
     *
     * @return the object name.
     */
    @Nullable
    @Override
    public String getPresentableText() {
        return null;
    }
}
