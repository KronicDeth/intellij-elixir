package org.elixir_lang.navigation.item_presentation;

import com.intellij.navigation.ItemPresentation;
import org.elixir_lang.icons.ElixirIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class Implementation implements ItemPresentation, Parent {
    /*
     * Fields
     */

    @NotNull
    private final String forName;
    @NotNull
    private final String protocolName;

    /*
     * Constructors
     */

    public Implementation(@NotNull String protocolName, @NotNull String forName) {
        this.forName = forName;
        this.protocolName = protocolName;
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
        return ElixirIcons.IMPLEMENTATION;
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
     * Returns the qualifier for the module created by the `defimpl` call.
     *
     * @return {@link #protocolName}.{@link #forName} without the last alias in {@link #forName}.
     */
    @Nullable
    @Override
    public String getLocationString() {
        String[] aliases = forName.split(".");
        StringBuilder locationStringBuilder = new StringBuilder(protocolName);

        // length - 1 to exclude the final element of aliases
        for (int i = 0; i < aliases.length - 1; i++) {
            locationStringBuilder.append('.');
            locationStringBuilder.append(aliases[i]);
        }

        return locationStringBuilder.toString();
    }

    /**
     * Return the unqualified alias name of the module created by the `defimpl` call.
     *
     * @return final alias in {@link #forName}
     */
    @Nullable
    @Override
    public String getPresentableText() {
        String[] aliases = forName.split(".");
        String presentableText;

        if (aliases.length == 0) {
            presentableText = forName;
        } else {
            presentableText = aliases[aliases.length - 1];
        }

        return  presentableText;
    }
}
