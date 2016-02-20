package org.elixir_lang.structure_view.element;

import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import org.elixir_lang.navigation.item_presentation.Parent;
import org.elixir_lang.psi.AtUnqualifiedNoParenthesesCall;
import org.elixir_lang.psi.ElixirAtIdentifier;
import org.elixir_lang.psi.call.Call;
import org.elixir_lang.psi.impl.ElixirPsiImplUtil;
import org.elixir_lang.structure_view.element.modular.Modular;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class Callback extends Element<AtUnqualifiedNoParenthesesCall> {
    /*
     * Fields
     */

    @NotNull
    private final Modular modular;

    /*
     * Static Methods
     */

    @Contract(pure = true)
    public static boolean is(@NotNull final Call call) {
        boolean is = false;

        if (call instanceof AtUnqualifiedNoParenthesesCall) {
            AtUnqualifiedNoParenthesesCall atUnqualifiedNoParenthesesCall = (AtUnqualifiedNoParenthesesCall) call;
            ElixirAtIdentifier atIdentifier = atUnqualifiedNoParenthesesCall.getAtIdentifier();

            ASTNode node = atIdentifier.getNode();
            ASTNode[] identifierNodes = node.getChildren(ElixirPsiImplUtil.IDENTIFIER_TOKEN_SET);

            assert identifierNodes.length == 1;

            ASTNode identifierNode = identifierNodes[0];
            String identifier = identifierNode.getText();

            if (identifier.equals("callback")) {
                is = true;
            }
        }

        return is;
    }

    /*
     * Constructors
     */

    public Callback(@NotNull Modular modular, Call navigationItem) {
        super((AtUnqualifiedNoParenthesesCall) navigationItem);
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
        Parent parentPresentation = (Parent) modular.getPresentation();
        String location = parentPresentation.getLocatedPresentableText();

        PsiElement[] arguments = ElixirPsiImplUtil.finalArguments(navigationItem);

        assert arguments != null;

        return new org.elixir_lang.navigation.item_presentation.Callback(location, arguments);
    }

}
