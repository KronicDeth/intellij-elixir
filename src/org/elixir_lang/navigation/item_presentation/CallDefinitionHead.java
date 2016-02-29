package org.elixir_lang.navigation.item_presentation;

import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.ui.RowIcon;
import org.elixir_lang.icons.ElixirIcons;
import org.elixir_lang.structure_view.element.Visible;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class CallDefinitionHead implements ItemPresentation {
    /*
     * Fields
     */

    @NotNull
    private final PsiElement psiElement;
    @NotNull
    private final NameArity callDefinition;
    @NotNull
    private final Visible.Visibility visibility;


    /*
     * Constructors
     */

    /**
     *
     * @param callDefinition the parent {@link NameArity} of which {@code call} is a head
     * @param psiElement a call definition head
     */
    public CallDefinitionHead(@NotNull NameArity callDefinition,
                              @NotNull Visible.Visibility visibility,
                              @NotNull PsiElement psiElement) {
        this.psiElement = psiElement;
        this.callDefinition = callDefinition;
        this.visibility = visibility;
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
        Icon[] icons = new Icon[]{
                ElixirIcons.Time.from(callDefinition.time()),
                ElixirIcons.Visibility.from(visibility),
                ElixirIcons.CALL_DEFINITION_CLAUSE
        };

        RowIcon rowIcon = new RowIcon(icons.length);

        for (int layer = 0; layer < icons.length; layer++) {
            rowIcon.setIcon(icons[layer], layer);
        }

        return rowIcon;
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
     * Returns the name of the object to be presented in most renderers across the program.
     *
     * @return the object name.
     */
    @NotNull
    @Override
    public String getPresentableText() {
        return psiElement.getText();
    }
}
