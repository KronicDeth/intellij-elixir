package org.elixir_lang.navigation.item_presentation.modular;

import com.intellij.icons.AllIcons;
import com.intellij.psi.PsiElement;
import org.elixir_lang.icons.ElixirIcons;
import org.elixir_lang.psi.call.Call;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class Unknown extends Module {
    /*
     * Constructors
     */

    /**
     * @param location the parent name of the Module that scopes {@code call}; {@code null} when scope is {@code quote}.
     * @param call     a {@code <module>.def<suffix>/2} call nested in {@code parent}.
     */
    public Unknown(@Nullable String location, @NotNull Call call) {
        super(location, call);
    }

    /*
     * Instance Methods
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
        String presentableText;

        if (primaryArguments != null && primaryArguments.length > 0) {
            presentableText = primaryArguments[0].getText();
        } else {
            presentableText = call.functionName();

            if (presentableText == null) {
                presentableText = "?";
            }
        }

        return presentableText;
    }

    /**
     * The module icon.
     *
     * @param unused Used to mean if open/close icons for tree renderer. No longer in use. The parameter is only there for API compatibility reason.
     */
    @NotNull
    @Override
    public Icon getIcon(boolean unused) {
        return AllIcons.General.QuestionDialog;
    }
}
