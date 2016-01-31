package org.elixir_lang.navigation.item_presentation;

import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.util.text.StringUtil;
import org.elixir_lang.icons.ElixirIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class Exception implements ItemPresentation {
    /*
     * Fields
     */

    @NotNull
    private final String[] fieldNames;
    @NotNull
    private final Module module;

    /*
     * Constructors
     */

    public Exception(@NotNull Module module, @NotNull String[] fieldNames) {
        this.fieldNames = fieldNames;
        this.module = module;
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
        return ElixirIcons.EXCEPTION;
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

        if (module != null) {
            String moduleLocationString = module.getLocationString();
            String modulePresentableText = module.getPresentableText();

            if (moduleLocationString != null) {
                locationString = moduleLocationString + "." + modulePresentableText;
            } else {
                locationString = modulePresentableText;
            }
        }

        return locationString;
    }

    /**
     * Returns the name of the object to be presented in most renderers across the program.
     *
     * @return the object name.
     */
    @Nullable
    @Override
    public String getPresentableText() {
        return "defexception [" + StringUtil.join(fieldNames, ", ") + "]";
    }
}
