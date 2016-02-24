package org.elixir_lang.structure_view.element.structure;

import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import org.elixir_lang.navigation.item_presentation.Parent;
import org.elixir_lang.psi.ElixirNoParenthesesKeywords;
import org.elixir_lang.psi.ElixirNoParenthesesOneArgument;
import org.elixir_lang.psi.QuotableKeywordList;
import org.elixir_lang.psi.QuotableKeywordPair;
import org.elixir_lang.psi.call.Call;
import org.elixir_lang.psi.call.arguments.NoParenthesesOneArgument;
import org.elixir_lang.structure_view.element.Element;
import org.jetbrains.annotations.NotNull;

/**
 * A {@code defstruct} field from a keyword pair
 */
public class FieldWithDefaultValue extends Element<QuotableKeywordPair> {
    /*
     * Fields
     */

    @NotNull
    private final Structure structure;

    /*
     * Static Methods
     */

    public static boolean is(QuotableKeywordPair quotableKeywordPair) {
        boolean fieldWithDefaultValue = false;

        PsiElement parent = quotableKeywordPair.getParent();

        if (parent instanceof QuotableKeywordList) {
            PsiElement grandParent = parent.getParent();

            if (grandParent instanceof ElixirNoParenthesesOneArgument) {
                PsiElement greatGrandParent = grandParent.getParent();

                if (greatGrandParent instanceof Call) {
                    Call greatGrandParentCall = (Call) greatGrandParent;
                    fieldWithDefaultValue = Structure.is(greatGrandParentCall);
                }
            }
        }

        return fieldWithDefaultValue;
    }

    /*
     * Constructors
     */

    public FieldWithDefaultValue(@NotNull Structure structure, @NotNull QuotableKeywordPair fieldDefaultValuePair) {
        super(fieldDefaultValuePair);
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

        PsiElement name =  navigationItem.getKeywordKey();
        PsiElement defaultValue = navigationItem.getKeywordValue();

        return new org.elixir_lang.navigation.item_presentation.structure.Field(
                location,
                name.getText(),
                defaultValue.getText()
        );
    }
}
