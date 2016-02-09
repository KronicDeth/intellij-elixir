package org.elixir_lang.structure_view.element;

import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import org.elixir_lang.navigation.item_presentation.Parent;
import org.elixir_lang.psi.call.Call;
import org.elixir_lang.psi.impl.ElixirPsiImplUtil;
import org.elixir_lang.structure_view.element.modular.Modular;
import org.jetbrains.annotations.NotNull;

public class Use extends Element<Call> {
    /*
     * Fields
     */

    @NotNull
    private final Modular modular;

    /*
     * Static Methods
     */

    public static boolean is(Call call) {
        return call.isCalling("Elixir.Kernel", "use", 1) || call.isCalling("Elixir.Kernel", "use", 2);
    }

    /*
     * Constructors
     */

    public Use(@NotNull Modular modular, Call navigationItem) {
        super(navigationItem);
        this.modular = modular;
    }

    /*
     * Instance Methods
     */

    /**
     * Returns the list of children of the tree element.
     *
     * @return the list of children.
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
        ItemPresentation itemPresentation = modular.getPresentation();
        String location = null;

        if (itemPresentation instanceof Parent) {
            Parent parentPresenation = (Parent) itemPresentation;
            location = parentPresenation.getLocatedPresentableText();
        }

        PsiElement[] arguments = ElixirPsiImplUtil.finalArguments(navigationItem);

        return new org.elixir_lang.navigation.item_presentation.Use(location, arguments);
    }
}
