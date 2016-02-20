package org.elixir_lang.navigation.item_presentation;

import com.intellij.navigation.ItemPresentation;
import com.intellij.ui.RowIcon;
import org.elixir_lang.icons.ElixirIcons;
import org.elixir_lang.psi.call.Call;
import org.elixir_lang.structure_view.element.Timed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class CallDefinitionSpecification implements ItemPresentation {
    /*
     * Fields
     */

    @Nullable
    private final Call specification;
    @NotNull
    private final Timed.Time time;

    /*
     * Constructors
     */

    /**
     * @param specification should only be {@code null} when format is unrecognized because it is invalid or mid-editing
     */
    public CallDefinitionSpecification(@Nullable Call specification, Timed.Time time) {
        this.specification = specification;
        this.time = time;
    }

    /**
     * Returns the icon representing the object.
     *
     * @param unused Used to mean if open/close icons for tree renderer. No longer in use. The parameter is only there for API compatibility reason.
     */
    @Nullable
    @Override
    public Icon getIcon(boolean unused) {
        Icon[] icons = new Icon[]{
                ElixirIcons.Time.from(time),
                ElixirIcons.Visibility.PUBLIC,
                ElixirIcons.SPECIFICATION
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
        return null;
    }

    /**
     * Returns the name of the object to be presented in most renderers across the program.
     *
     * @return the object name.
     */
    @Nullable
    @Override
    public String getPresentableText() {
        String presentableText = null;

        if (specification != null) {
            presentableText = specification.getText();
        }

        return presentableText;
    }

}
