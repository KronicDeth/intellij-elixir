package org.elixir_lang.structure_view.element;

import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.util.*;
import com.intellij.util.containers.ContainerUtil;
import org.elixir_lang.psi.UnqualifiedParenthesesCall;
import org.elixir_lang.psi.call.Call;
import org.elixir_lang.psi.impl.ElixirPsiImplUtil;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FunctionDelegation extends Element<Call> {
    /*
     * Fields
     */

    private final Delegation delegation;

    /*
     * Static Methods
     */

    @Contract(pure = true)
    public static boolean is(@NotNull final Call call) {
        return call instanceof UnqualifiedParenthesesCall;
    }

    /*
     * Constructors
     */

    public FunctionDelegation(Delegation delegation, Call navigationItem) {
        super(navigationItem);
        this.delegation = delegation;
    }

    /*
     * Instance Methods
     */

    /**
     * The arguments, in the order declared.
     *
     * @return {@code {ARG1, ARG2, ..., ARGN}} in {@code defdelegate NAME(ARG1, ARG2, ... ARGN), ...} or
     *   {@code defdelegate [NAME(ARG1, ARG2, ..., ARGN), ...], ...}.
     */
    @Nullable
    public PsiElement[] arguments() {
        return ElixirPsiImplUtil.finalArguments(navigationItem);
    }

    /**
     * Function Delegations have no children
     *
     * @return empty
     */
    @NotNull
    @Override
    public TreeElement[] getChildren() {
        return new TreeElement[0];
    }

    /**
     * Returns the presentation of the tree element.
     *
     * @return the element presentation.
     */
    @NotNull
    @Override
    public ItemPresentation getPresentation() {
        PsiElement[] arguments = arguments();
        String[] argumentNames;

        if (arguments != null) {
            int length = arguments.length;
            argumentNames = new String[length];

            for (int i = 0; i < length; i++) {
                argumentNames[i] = arguments[i].getText();
            }
        } else {
            argumentNames = new String[0];
        }

        return new org.elixir_lang.navigation.item_presentation.FunctionDelegation(
                (org.elixir_lang.navigation.item_presentation.Delegation) delegation.getPresentation(),
                name(),
                argumentNames
        );
    }

    /**
     * The name function being created by the `defdelegate`.
     *
     * @return {@code NAME} in {@code defdelegate NAME(ARGUMENT...), ...} or
     *   {@code defdelegate [NAME(ARGUMENT, ...), ...], ...}.
     */
    @Nullable
    public String name() {
        return navigationItem.functionName();
    }
}
