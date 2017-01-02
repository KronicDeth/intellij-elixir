package org.elixir_lang.navigation.item_presentation;

import com.intellij.navigation.ItemPresentation;
import org.elixir_lang.name_arity.PresentationData;
import org.elixir_lang.structure_view.element.Timed;
import org.elixir_lang.structure_view.element.Visible;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * The name/arity of a {@link org.elixir_lang.structure_view.element.CallDefinition} or
 * {@link org.elixir_lang.structure_view.element.CallReference}.
 */
public class NameArity implements ItemPresentation {
    /*
     * Fields
     */

    @Nullable
    private final Integer arity;
    private final boolean callback;
    @Nullable
    private final String location;
    @NotNull
    private final String name;
    // is allowed to be overridden by an override function
    private final boolean overridable;
    // overrides an overridable function
    private boolean override;
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
    public NameArity(@Nullable String location,
                     boolean callback,
                     @NotNull Timed.Time time,
                     @Nullable Visible.Visibility visibility,
                     boolean overridable,
                     boolean override,
                     @NotNull String name,
                     @Nullable Integer arity) {
        this.arity = arity;
        this.callback = callback;
        this.location = location;
        this.name = name;
        this.overridable = overridable;
        this.override = override;
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
        return PresentationData.presentableText(name, arity);
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
        return PresentationData.icon(callback, overridable, override, time, visibility);
    }

    public Timed.Time time() {
        return time;
    }
}
