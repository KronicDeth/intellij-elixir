package org.elixir_lang.navigation.item_presentation;

import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.ui.RowIcon;
import com.intellij.util.PlatformIcons;
import org.elixir_lang.icons.ElixirIcons;
import org.elixir_lang.psi.call.Call;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class Function implements ItemPresentation {
    /*
     * Fields
     */

    private final Call call;
    private final Module module;

    /*
     * Constructors
     */

    /**
     *
     * @param module the parent {@link Module} in which the function is declared
     * @param call a {@code Kernel.def/2} call nested in {@code parent}
     */
    public Function(@NotNull Module module, @NotNull Call call) {
        this.module = module;
        this.call = call;
    }

    /*
     * Public Instance Methods
     */

    /**
     * Returns the name of the object to be presented in most renderers across the program.
     *
     * @return the function name.
     */
    @NotNull
    @Override
    public String getPresentableText() {
        PsiElement[] primaryArguments = call.primaryArguments();

        assert primaryArguments.length > 0;

        return primaryArguments[0].getText();
    }

    /**
     * Returns the location of the object (for example, the package of a class). The location
     * string is used by some renderers and usually displayed as grayed text next to the item name.
     *
     * @return the location description, or null if none is applicable.
     */
    @NotNull
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
     * Returns the icon representing the object.
     *
     * @param unused Used to mean if open/close icons for tree renderer. No longer in use. The parameter is only there for API compatibility reason.
     */
    @Nullable
    @Override
    public Icon getIcon(boolean unused) {
        RowIcon rowIcon = new RowIcon(2);
        rowIcon.setIcon(ElixirIcons.FUNCTION, 0);
        rowIcon.setIcon(PlatformIcons.PUBLIC_ICON, 1);

        return rowIcon;
    }
}
