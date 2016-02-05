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

public class CallDefinitionClause implements ItemPresentation {
    /*
     * Fields
     */

    private final Call call;
    private final CallDefinition callDefinition;

    /*
     * Constructors
     */

    /**
     *
     * @param call a {@code Kernel.def/2} call nested in {@code parent}
     * @param callDefinition the parent {@link CallDefinition} of which {@code call} is a clause
     */
    public CallDefinitionClause(@NotNull CallDefinition callDefinition, @NotNull Call call) {
        this.call = call;
        this.callDefinition = callDefinition;
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

        assert primaryArguments != null;
        assert primaryArguments.length > 0;

        return primaryArguments[0].getText();
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
        return callDefinition.getLocationString();
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
        Icon timeIcon = null;

        switch (callDefinition.time()) {
            case COMPILE:
                timeIcon = ElixirIcons.MACRO_CLAUSE;
                break;
            case RUN:
                timeIcon = ElixirIcons.FUNCTION_CLAUSE;
                break;
        }

        assert timeIcon != null;

        rowIcon.setIcon(timeIcon, 0);
        rowIcon.setIcon(PlatformIcons.PUBLIC_ICON, 1);

        return rowIcon;
    }
}
