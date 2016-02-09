package org.elixir_lang.navigation.item_presentation;

import com.intellij.codeInsight.template.postfix.templates.StringBasedPostfixTemplate;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import org.elixir_lang.icons.ElixirIcons;
import org.elixir_lang.psi.call.Call;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class Use implements ItemPresentation {
    /*
     * Fields
     */

    @NotNull
    private final PsiElement[] arguments;
    @Nullable
    private final String location;

    /*
     * Constructors
     */

    public Use(@Nullable  String location, @NotNull PsiElement[] arguments) {
        assert arguments.length >= 1;

        this.arguments = arguments;
        this.location = location;
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
        // TODO make a custom `ElixirIcons.USE` Icon
        return ElixirIcons.Time.COMPILE;
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
        return location;
    }

    /**
     * Returns the `use <ARGUMENTS>`
     *
     * @return the object name.
     */
    @Nullable
    @Override
    public String getPresentableText() {
        StringBuilder presentableTextBuilder = new StringBuilder("use ");

        for (int i = 0; i < arguments.length; i++) {
            if (i > 0) {
                presentableTextBuilder.append(", ");
            }

            presentableTextBuilder.append(arguments[i].getText());
        }

        return presentableTextBuilder.toString();
    }
}
