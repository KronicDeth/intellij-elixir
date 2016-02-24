package org.elixir_lang.structure_view.element.structure;

import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import org.elixir_lang.navigation.item_presentation.Parent;
import org.elixir_lang.psi.ElixirAccessExpression;
import org.elixir_lang.psi.ElixirAtom;
import org.elixir_lang.psi.ElixirList;
import org.elixir_lang.psi.ElixirNoParenthesesOneArgument;
import org.elixir_lang.psi.call.Call;
import org.elixir_lang.structure_view.element.Element;
import org.jetbrains.annotations.NotNull;

/**
 * A {@code defstruct} field only given by its name
 */
public class Field extends Element<ElixirAtom> {
    /*
     * Fields
     */

    @NotNull
    private final Structure structure;

    /*
     * Static Methods
     */

    public static boolean is(ElixirAtom atom) {
        boolean field = false;
        PsiElement parent = atom.getParent();

        if (parent instanceof ElixirAccessExpression && parent.getChildren().length == 1) {
            PsiElement grandParent = parent.getParent();

            if (grandParent instanceof ElixirList) {
                PsiElement greatGrandParent = grandParent.getParent();

                if (greatGrandParent instanceof ElixirAccessExpression && greatGrandParent.getChildren().length == 1) {
                    PsiElement greatGreatGrandParent = greatGrandParent.getParent();

                    if (greatGreatGrandParent instanceof ElixirNoParenthesesOneArgument) {
                        PsiElement greatGreatGreatGrandParent = greatGreatGrandParent.getParent();

                        if (greatGreatGreatGrandParent instanceof Call) {
                            Call greatGreatGreatGrandParentCall = (Call) greatGreatGreatGrandParent;

                            field = Structure.is(greatGreatGreatGrandParentCall);
                        }
                    }
                }
            }
        }

        return field;
    }

    /*
     * Constructors
     */

    public Field(@NotNull Structure structure, @NotNull ElixirAtom name) {
        super(name);
        this.structure = structure;
    }

    /*
     * Instance Methods
     */

    /**
     * No children
     *
     * @return empty array
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
        Parent parentPresentation = (Parent) structure.getPresentation();
        String location = parentPresentation.getLocatedPresentableText();
        String name;

        String atomText = navigationItem.getText();
        name = atomText.substring(1);

        return new org.elixir_lang.navigation.item_presentation.structure.Field(location, name);
    }
}
