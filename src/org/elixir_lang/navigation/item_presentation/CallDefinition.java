package org.elixir_lang.navigation.item_presentation;

import com.intellij.navigation.ItemPresentation;
import com.intellij.ui.RowIcon;
import com.intellij.util.PlatformIcons;
import org.elixir_lang.icons.ElixirIcons;
import org.elixir_lang.structure_view.element.Timed;
import org.elixir_lang.structure_view.element.Visible;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class CallDefinition implements ItemPresentation {
    /*
     * Fields
     */

    private final int arity;
    @Nullable
    private final String location;
    @NotNull
    private final String name;
    @NotNull
    private final Timed.Time time;
    @Nullable
    private final Visible.Visibility visibility;

    /**
     *
     * @param location
     * @param time
     * @param visibility {@code null} if clauses are a mix of private and public
     * @param name
     * @param arity
     */
    public CallDefinition(@Nullable String location,
                          @NotNull Timed.Time time,
                          @Nullable Visible.Visibility visibility,
                          @NotNull String name,
                          int arity) {
        this.arity = arity;
        this.location = location;
        this.name = name;
        this.time = time;
        this.visibility = visibility;
    }

    /**
     * The name/arity of this function.
     *
     * @return {@code name}/{@code arity}
     */
    @NotNull
    @Override
    public String getPresentableText() {
        return name + "/" + arity;
    }

    /**
     * Returns the module name where the function is defined
     *
     * @return the module name or {@code null} for {@code quote}.
     */
    @Nullable
    @Override
    public String getLocationString() {
        return location;
    }

    /**
     * Returns the icon representing the object.
     *
     * @param unused Used to mean if open/close icons for tree renderer. No longer in use. The parameter is only there for API compatibility reason.
     */
    @NotNull
    @Override
    public Icon getIcon(boolean unused) {
        RowIcon rowIcon = new RowIcon(3);
        Icon timeIcon = null;

        switch (time) {
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
            default:
                visibilityIcon = ElixirIcons.UNKNOWN;
        }

        rowIcon.setIcon(timeIcon, 0);
        rowIcon.setIcon(visibilityIcon, 1);
        rowIcon.setIcon(ElixirIcons.CALL_DEFINITION, 2);

        return rowIcon;
    }

    public Timed.Time time() {
        return time;
    }
}
