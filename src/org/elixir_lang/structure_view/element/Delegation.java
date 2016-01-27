package org.elixir_lang.structure_view.element;

import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.elixir_lang.psi.ElixirAccessExpression;
import org.elixir_lang.psi.ElixirList;
import org.elixir_lang.psi.call.Call;
import org.elixir_lang.psi.impl.ElixirPsiImplUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Delegation extends Element<Call>  {
    /*
     * Fields
     */

    @NotNull
    private final Module module;

    /*
     * Static Methods
     */

    public static boolean is(Call call) {
        return call.isCalling("Elixir.Kernel", "defdelegate", 2);
    }

    /*
     * Constructors
     */

    public Delegation(@NotNull Module module, @NotNull Call call) {
        super(call);
        this.module = module;
    }

    /*
     * Instance Methods
     */

    /**
     * If {@code true}, when delegated, the first argument passed to the delegated function will be relocated to the end
     * of the arguments when dispatched to the target.
     *
     * @return defaults to {@code false} and when keyword argument is not parsable as boolean.
     */
    public boolean appendFirst() {
        PsiElement keywordValue = ElixirPsiImplUtil.keywordArgument(navigationItem, "append_first");
        boolean appendFirst = false;

        if (keywordValue != null) {
            String keywordValueText = keywordValue.getText();

            if (keywordValueText.equals("true")) {
                appendFirst = true;
            }
        }

        return appendFirst;
    }

    /**
     * The value of the {@code :as} keyword argument
     *
     * @return text of the {@code :as} keyword value
     */
    @Nullable
    public String as() {
        return keywordArgumentText("as");
    }

    /**
     * Returns the list of children of the tree element.
     *
     * @return the list of children.
     */
    @NotNull
    @Override
    public TreeElement[] getChildren() {
        PsiElement[] finalArguments = ElixirPsiImplUtil.finalArguments(navigationItem);

        assert finalArguments != null;
        assert finalArguments.length > 0;

        TreeElement[] children = null;
        PsiElement firstFinalArgument = finalArguments[0];

        if (firstFinalArgument instanceof ElixirAccessExpression) {
            ElixirAccessExpression accessExpression = (ElixirAccessExpression) firstFinalArgument;

            PsiElement[] accessExpressionChildren = accessExpression.getChildren();

            if (accessExpressionChildren.length == 1) {
                PsiElement accessExpressionChild = accessExpressionChildren[0];

                if (accessExpressionChild instanceof ElixirList) {
                    ElixirList list = (ElixirList) accessExpressionChild;

                    Collection<Call> listCalls = PsiTreeUtil.findChildrenOfType(list, Call.class);
                    List<TreeElement> treeElementList = new ArrayList<TreeElement>(listCalls.size());

                    for (Call listCall : listCalls) {
                        if (FunctionDelegation.is(listCall)) {
                            treeElementList.add(new FunctionDelegation(this, listCall));
                        }
                    }

                    children = treeElementList.toArray(new TreeElement[treeElementList.size()]);
                }
            }
        } else {
            assert firstFinalArgument != null;
        }

        if (children == null) {
            children = new TreeElement[0];
        }

        return children;
    }

    /**
     * Returns the presentation of the tree element.
     *
     * @return the element presentation.
     */
    @NotNull
    @Override
    public ItemPresentation getPresentation() {
        return new org.elixir_lang.navigation.item_presentation.Delegation(
                (org.elixir_lang.navigation.item_presentation.Module) module.getPresentation(),
                to(),
                as(),
                appendFirst()
        );
    }

    /**
     * The value of the {@code :to} keyword argument
     *
     * @return text of the {@code :to} keyword value
     */
    @Nullable
    public String to() {
        return keywordArgumentText("to");
    }

    /*
     * Private Instance Methods
     */

    /**
     * The text of the {@code keywordValueText} keyword argument.
     *
     * @param keywordValueText the text of the keyword value
     * @return the {@code PsiElement.getText()} of the keyword value if the keyword argument exists of the
     *   {@link Element#navigationItem}; {@code null} if the keyword argument does not exist.
     */
    @Nullable
    private String keywordArgumentText(@NotNull final String keywordValueText) {
        PsiElement keywordValue = ElixirPsiImplUtil.keywordArgument(navigationItem, keywordValueText);
        String text = null;

        if (keywordValue != null) {
            text = keywordValue.getText();
        }

        return text;
    }
}
