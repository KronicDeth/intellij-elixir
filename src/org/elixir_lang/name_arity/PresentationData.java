package org.elixir_lang.name_arity;

import com.intellij.ui.RowIcon;
import org.elixir_lang.Icons;
import org.elixir_lang.Visibility;
import org.elixir_lang.structure_view.element.Timed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class PresentationData {
    @NotNull
    public static Icon icon(final boolean callback,
                            final boolean overridable,
                            final boolean override,
                            @NotNull final Timed.Time time,
                            @Nullable final Visibility visibility) {
        int layers = 3;

        if (callback) {
            layers++;
        }

        if (overridable) {
            layers++;
        }

        if (override) {
            layers++;
        }

        RowIcon rowIcon = new RowIcon(layers);

        int layer = 0;

        if (callback) {
            rowIcon.setIcon(Icons.CALLBACK, layer++);
        }

        Icon timeIcon = Icons.Time.from(time);
        rowIcon.setIcon(timeIcon, layer++);

        Icon visibilityIcon = Icons.Visibility.from(visibility);
        rowIcon.setIcon(visibilityIcon, layer++);

        rowIcon.setIcon(Icons.CALL_DEFINITION, layer++);

        if (overridable) {
            rowIcon.setIcon(Icons.OVERRIDABLE, layer++);
        }

        if (override) {
            rowIcon.setIcon(Icons.OVERRIDE, layer);
        }

        return rowIcon;
    }

    @NotNull
    public static String presentableText(@NotNull String name, @Nullable Integer arity) {
        StringBuilder presentableTextBuilder = new StringBuilder(name).append('/');

        if (arity != null) {
            presentableTextBuilder.append(arity);
        } else {
            presentableTextBuilder.append('?');
        }

        return presentableTextBuilder.toString();
    }
}
