package org.elixir_lang.navigation.item_presentation.structure;

import com.intellij.navigation.ItemPresentation;
import org.elixir_lang.icons.ElixirIcons;
import org.elixir_lang.navigation.item_presentation.Parent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class Structure implements ItemPresentation, Parent {
    /*
     * Fields
     */

    @Nullable
    private final String location;
    @NotNull
    private final String name;

    /*
     * Constructors
     */

    public Structure(@Nullable String location, @NotNull String name) {
        this.location = location;
        this.name = name;
    }

    /*
     * Instance Methods
     */

    /**
     * Returns the icon representing the object.
     *
     * @param unused Used to mean if open/close icons for tree renderer. No longer in use. The parameter is only there for API compatibility reason.
     */
    @NotNull
    @Override
    public Icon getIcon(boolean unused) {
        return ElixirIcons.STRUCTURE;
    }

    /**
     * Outer module of the fully-qualified struct name.
     *
     *
     * @return the parent module or {@code null} if there are no {@code .} in fully-qualified name
     */
    @Nullable
    @Override
    public String getLocationString() {
        return location;
    }

    /**
     * Combines {@link #getLocationString()} with {@link #getPresentableText()} for when this Structure is the parent of
     * a {@link Field}.
     *
     * @return {@link #getLocationString()} + "." + {@link #getPresentableText()}
     */
    @Override
    @NotNull
    public String getLocatedPresentableText() {
        StringBuilder locatedPresentableText = new StringBuilder("%");

        if (location != null) {
            locatedPresentableText.append(location).append('.');
        }

        locatedPresentableText.append(name).append("{}");

        return locatedPresentableText.toString();
    }

    /**
     * The unqualified name of the struct
     *
     * @return the object name.
     */
    @NotNull
    @Override
    public String getPresentableText() {
        return "%" + name + "{}";
    }
}
