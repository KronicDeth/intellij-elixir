package org.elixir_lang.structure_view.element.structure;

import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.NavigatablePsiElement;
import org.elixir_lang.navigation.item_presentation.Parent;
import org.elixir_lang.psi.ElixirAtom;
import org.elixir_lang.structure_view.element.Element;
import org.jetbrains.annotations.NotNull;

/**
 * A {@code defstruct} field only given by its name
 */
public class Field extends Element<NavigatablePsiElement> {
    /*
     * Fields
     */

    @NotNull
    private final Structure structure;

    /*
     * Constructors
     */

    public Field(@NotNull Structure structure, @NotNull NavigatablePsiElement name) {
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

        if (navigationItem instanceof ElixirAtom) {
            ElixirAtom atom = (ElixirAtom) navigationItem;
            String atomText = atom.getText();
            name = atomText.substring(1);
        } else {
            name = navigationItem.getText();
        }

        return new org.elixir_lang.navigation.item_presentation.structure.Field(location, name);
    }
}
