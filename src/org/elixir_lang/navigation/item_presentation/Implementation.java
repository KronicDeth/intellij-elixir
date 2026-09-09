package org.elixir_lang.navigation.item_presentation;

import com.intellij.navigation.ItemPresentation;
import org.elixir_lang.Icons;
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
        return Icons.Implementation.Structure;
    }

    /**
     * Combines {@link #getLocationString()} with {@link #getPresentableText()} for when this is the parent of
     * an {@link ItemPresentation} and needs to act as the
     * {@link ItemPresentation#getLocationString()}.
     *
     * @return {@link #getLocationString()} + "." + {@link #getPresentableText()}
     */
    @NotNull
    @Override
    public String getLocatedPresentableText() {
        return getLocationString() + "." + getPresentableText();
    }

    @NotNull
    @Override
    public String getLocationString() {
        return protocolName;
    }

    @NotNull
    @Override
    public String getPresentableText() {
        return forName;
    }
}
