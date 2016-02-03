package org.elixir_lang.navigation.item_presentation;

import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.util.text.StringUtil;
import org.elixir_lang.icons.ElixirIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class FunctionDelegation implements ItemPresentation {
    /*
     * Fields
     */

    @NotNull
    private final String[] argumentNames;
    @NotNull
    private final Delegation delegation;
    @Nullable
    private final String name;

    /*
     * Constructors
     */

    public FunctionDelegation(@NotNull Delegation delegation, @Nullable String name, @NotNull String[] argumentNames) {
        this.argumentNames = argumentNames;
        this.delegation = delegation;
        this.name = name;
    }

    /*
     * Instance Methods
     */

    /**
     * The name of the function name to call on {@code #delegation} {@code Delegation#to}.
     *
     * @return {@code #name} if not set; otherwise, {@code as}.
     */
    @NotNull
    public String as() {
        String as = delegation.as();

        if (as == null) {
            as = name();
        }

        return as;
    }

    /**
     * Returns the name of the object to be presented in most renderers across the program.
     *
     * @return the object name.
     */
    @Nullable
    @Override
    public String getPresentableText() {
        StringBuilder presentableText = new StringBuilder(name());
        presentableText.append('(');

        presentableText.append(StringUtil.join(argumentNames, ", "));
        presentableText.append("), do: ");

        presentableText.append(delegation.to());
        presentableText.append('.');

        presentableText.append(as());
        presentableText.append('(');

        int start;

        if (delegation.appendFirst()) {
            start = 1;
        } else {
            start = 0;
        }

        for (int i = 0; i < argumentNames.length; i++) {
            if (i > 0) {
                presentableText.append(", ");
            }

            int index = (start + i) % argumentNames.length;
            presentableText.append(argumentNames[index]);
        }

        presentableText.append(')');

        return presentableText.toString();
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
        return delegation.getLocationString();
    }

    /**
     * Returns the icon representing the object.
     *
     * @param unused Used to mean if open/close icons for tree renderer. No longer in use. The parameter is only there for API compatibility reason.
     */
    @Nullable
    @Override
    public Icon getIcon(boolean unused) {
        return ElixirIcons.FUNCTION_DELEGATION;
    }

    public String name() {
        String presentable = name;

        if (name == null) {
            presentable = "?";
        }

        return presentable;
    }
}
