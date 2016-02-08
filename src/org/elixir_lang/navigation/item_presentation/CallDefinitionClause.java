package org.elixir_lang.navigation.item_presentation;

import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.ui.RowIcon;
import org.elixir_lang.icons.ElixirIcons;
import org.elixir_lang.psi.call.Call;
import org.elixir_lang.structure_view.element.Visible;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class CallDefinitionClause implements ItemPresentation {
    /*
     * Fields
     */

    private final Call call;
    private final NameArity callDefinition;
    private final Visible.Visibility visibility;

    /*
     * Constructors
     */

    /**
     *
     * @param call a {@code Kernel.def/2} call nested in {@code parent}
     * @param callDefinition the parent {@link NameArity} of which {@code call} is a clause
     */
    public CallDefinitionClause(@NotNull NameArity callDefinition,
                                @NotNull Visible.Visibility visibility,
                                @NotNull Call call) {
        this.call = call;
        this.callDefinition = callDefinition;
        this.visibility = visibility;
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
        RowIcon rowIcon = new RowIcon(3);
        Icon timeIcon = null;

        switch (callDefinition.time()) {
            case COMPILE:
                timeIcon = ElixirIcons.Time.COMPILE;
                break;
            case RUN:
                timeIcon = ElixirIcons.Time.RUN;
                break;
        }

        assert timeIcon != null;

        Icon visibilityIcon = null;

        switch (visibility) {
            case PRIVATE:
                visibilityIcon = ElixirIcons.Visibility.PRIVATE;
                break;
            case PUBLIC:
                visibilityIcon = ElixirIcons.Visibility.PUBLIC;
                break;
        }

        assert visibilityIcon != null;

        rowIcon.setIcon(timeIcon, 0);
        rowIcon.setIcon(visibilityIcon, 1);
        rowIcon.setIcon(ElixirIcons.CALL_DEFINITION_CLAUSE, 2);

        return rowIcon;
    }
}
