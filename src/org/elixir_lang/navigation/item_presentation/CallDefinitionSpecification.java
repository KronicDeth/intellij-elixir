package org.elixir_lang.navigation.item_presentation;

import com.intellij.navigation.ItemPresentation;
import com.intellij.ui.RowIcon;
import org.elixir_lang.Icons;
import org.elixir_lang.psi.call.Call;
import org.elixir_lang.structure_view.element.Timed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class CallDefinitionSpecification implements ItemPresentation {
    /*
     * Fields
     */

    private final boolean callback;
    @Nullable
    private final String location;
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
    public CallDefinitionSpecification(@Nullable String location,
                                       @Nullable Call specification,
                                       boolean callback,
                                       @NotNull Timed.Time time) {
        this.callback = callback;
        this.location = location;
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
        int layers = 3;

        if (callback) {
            layers++;
        }

        RowIcon rowIcon = new RowIcon(layers);

        int layer = 0;

        if (callback) {
            rowIcon.setIcon(Icons.CALLBACK, layer++);
        }

        Icon timeIcon = Icons.Time.from(time);
        rowIcon.setIcon(timeIcon, layer++);

        rowIcon.setIcon(Icons.Visibility.PUBLIC, layer++);
        rowIcon.setIcon(Icons.SPECIFICATION, layer);

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
        return location;
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
