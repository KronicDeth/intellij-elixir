package org.elixir_lang.navigation.item_presentation;

import com.intellij.navigation.ItemPresentation;
import org.elixir_lang.icons.ElixirIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class Delegation implements ItemPresentation {
    /*
     * CONSTANTS
     */

    /*
     * Fields
     */

    private final boolean appendFirst;
    @Nullable
    private final String as;
    @NotNull
    private final Module module;
    @Nullable
    private final String to;

    /*
     * Constructors
     */

    public Delegation(@NotNull Module module, @Nullable String to, @Nullable String as, boolean appendFirst) {
        this.appendFirst = appendFirst;
        this.as = as;
        this.module = module;
        this.to = to;
    }

    /*
     * Instance Methods
     */

    /**
     * Whether to rearrange the {@link FunctionDelegation#argumentNames}.
     *
     * @return {@code true} if the first {@link FunctionDelegation#argumentNames} should be moved to the end in
     *   {@link FunctionDelegation#getPresentableText()}; {@code false} if the {@link FunctionDelegation#argumentNames}
     *   should remain in given order.
     */
    public boolean appendFirst() {
        return appendFirst;
    }

    /**
     *
     */
    @Nullable
    public String as() {
        return as;
    }

    /**
     * The parts of the {@code defdelegate} call shared across all {@link FunctionDelegation}.
     *
     * @return {@code defdelegate append_first: (false|true), to: (to|?)}
     */
    @Nullable
    @Override
    public String getPresentableText() {
        return "defdelegate append_first: " +
                appendFirst +
                ", to: " +
                to();
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
        return module.getLocatedPresentableText();
    }

    /**
     * Returns the icon representing the object.
     *
     * @param unused Used to mean if open/close icons for tree renderer. No longer in use. The parameter is only there for API compatibility reason.
     */
    @Nullable
    @Override
    public Icon getIcon(boolean unused) {
        return ElixirIcons.DELEGATION;
    }

    /**
     * Presentable form of {@link #to}.
     *
     * @return {@code to} if it is not {@code null}; {@code "?"} if {@code null};
     */
    @NotNull
    public String to() {
        String presentable;

        if (to != null) {
            presentable = to;
        } else {
            presentable = "?";
        }

        return presentable;
    }
}
