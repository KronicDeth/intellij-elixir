package org.elixir_lang.navigation.item_presentation;

import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.ui.RowIcon;
import com.intellij.util.PlatformIcons;
import org.elixir_lang.icons.ElixirIcons;
import org.elixir_lang.psi.call.Call;
import org.elixir_lang.psi.impl.ElixirPsiImplUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class Quote implements ItemPresentation {
    /*
     * Fields
     */

    @NotNull
    private final Call call;
    @Nullable
    private final String location;

    /*
     * Constructors
     */

    public Quote(@Nullable String location, @NotNull Call call) {
        this.call = call;
        this.location = location;
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

        rowIcon.setIcon(ElixirIcons.Time.COMPILE, 0);
        rowIcon.setIcon(PlatformIcons.ANONYMOUS_CLASS_ICON, 1);

        return rowIcon;
    }

    /**
     * No location string as the quote location is the use site.
     *
     * @return {@code null} because a {@code quote} block's location is dependent on where it is injected
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
        StringBuilder presentableTextBuilder = new StringBuilder("quote");

        PsiElement locationValueElement = ElixirPsiImplUtil.keywordArgument(call, "location");
        PsiElement unquoteValueElement = ElixirPsiImplUtil.keywordArgument(call, "unquote");

        if (locationValueElement != null) {
            presentableTextBuilder.append(" location: ").append(locationValueElement.getText());

            if (unquoteValueElement != null) {
                presentableTextBuilder.append(",");
            }
        }

        if (unquoteValueElement != null) {
            presentableTextBuilder.append(", unquote: ").append(unquoteValueElement.getText());
        }

        // leave out :bind_quoted and :context because they aren't simple atoms

        return presentableTextBuilder.toString();
    }

}
