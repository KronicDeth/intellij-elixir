package org.elixir_lang.navigation.item_presentation;

import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.ui.RowIcon;
import org.elixir_lang.Visibility;
import org.elixir_lang.icons.ElixirIcons;
import org.elixir_lang.psi.ElixirMatchedWhenOperation;
import org.elixir_lang.psi.call.Call;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class Type implements ItemPresentation {
    /*
     * Fields
     */

    @Nullable
    private final String location;
    private final boolean opaque;
    @Nullable
    private final Call type;
    @NotNull
    private final Visibility visibility;

    /*
     * Static Methods
     */

    @Nullable static PsiElement head(@NotNull Call type) {
        PsiElement head = null;

        if (type instanceof org.elixir_lang.psi.operation.Type) {
            head = head((org.elixir_lang.psi.operation.Type) type);
        } else if (type instanceof ElixirMatchedWhenOperation) {
            head = head((ElixirMatchedWhenOperation) type);
        }

        return head;
    }

    @NotNull
    private static PsiElement head(@NotNull org.elixir_lang.psi.operation.Type typeOperation) {
        return typeOperation.leftOperand();
    }

    @Nullable
    private static PsiElement head(@NotNull ElixirMatchedWhenOperation whenOperation) {
        PsiElement head = null;

        PsiElement parameterizedType = whenOperation.leftOperand();

        if (parameterizedType instanceof org.elixir_lang.psi.operation.Type) {
            head = head((org.elixir_lang.psi.operation.Type) parameterizedType);
        }

        return head;
    }

    /*
     * Constructors
     */

    public Type(@Nullable String location, @Nullable Call type, boolean opaque, @NotNull Visibility visibility) {
        this.location = location;
        this.opaque = opaque;
        this.type = type;
        this.visibility = visibility;
    }

    /*
     *
     * Instance Methods
     *
     */

    /*
     * Public Instance Methods
     */

    /**
     * Returns the icon representing the object.
     *
     * @param unused Used to mean if open/close icons for tree renderer. No longer in use. The parameter is only there for API compatibility reason.
     */
    @NotNull
    @Override
    public Icon getIcon(boolean unused) {
        Icon[] icons = new Icon[]{
            ElixirIcons.Time.COMPILE,
            ElixirIcons.Visibility.from(visibility),
            ElixirIcons.TYPE
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
        return location;
    }

    /**
     * The type declaration.  The body (after {@code ::} is not shown if the type is {@link #opaque}.
     *
     * @return the type declaration.
     */
    @NotNull
    @Override
    public String getPresentableText() {
        String presentableText = "?";

        if (type != null) {
            if (opaque) {
                PsiElement head = head(type);

                if (head != null) {
                    presentableText = head.getText();
                }
            } else {
                presentableText = type.getText();
            }
        }

        return presentableText;
    }
}
