package org.elixir_lang.structure_view.element;

import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiElement;
import org.elixir_lang.psi.ElixirAccessExpression;
import org.elixir_lang.psi.ElixirList;
import org.elixir_lang.psi.call.Call;
import org.elixir_lang.psi.impl.ElixirPsiImplUtil;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * A `defexception` with its fields and the callbacks `exception/1` and `message/1` if overridden.
 */
public class Exception extends Element<Call> {
    /*
     * Fields
     */

    @Nullable
    private List<Function> callbacks = null;
    @NotNull
    private final Module module;

    /*
     * Static Methods
     */

    public static boolean is(Call call) {
        return call.isCalling("Elixir.Kernel", "defexception", 1);
    }

    public static boolean isCallback(Pair<String, Integer> nameArity) {
      return nameArity.second == 1 && (nameArity.first.equals("exception") || nameArity.first.equals("message"));
    }

    /*
     * Constructors
     */

    public Exception(@NotNull Module module, @NotNull Call call) {
        super(call);
        this.module = module;
    }

    /*
     * Instance Methods
     */

    /**
     * Adds callback function
     *
     * @param callback the callback function: either exception/1 or message/1.
     */
    @Contract(pure = false)
    public void callback(@NotNull final Function callback) {
        assert callback.arity() == 1;

        String callbackName = callback.name();
        assert callbackName.equals("exception") || callbackName.equals("message");

        if (callbacks == null) {
            callbacks = new ArrayList<Function>();
        }

        callbacks.add(callback);
    }

    /**
     * The fields for the struct for the defined exception.
     *
     * @return `{FIELD1, ...}` in `defexception [FIELD1, ...]`
     */
    @NotNull
    public PsiElement[] fields() {
        PsiElement[] finalArguments = ElixirPsiImplUtil.finalArguments(navigationItem);

        assert finalArguments.length == 1;

        PsiElement finalArgument = finalArguments[0];

        assert finalArgument instanceof ElixirAccessExpression;

        ElixirAccessExpression accessExpression = (ElixirAccessExpression) finalArgument;
        PsiElement[] accessExpressionChildren = accessExpression.getChildren();

        assert accessExpressionChildren.length == 1;

        PsiElement accessExpressionChild = accessExpressionChildren[0];

        assert accessExpressionChild instanceof ElixirList;

        ElixirList list = (ElixirList) accessExpressionChild;

        return list.getChildren();
    }

    /**
     * Returns the list of children of the tree element.
     *
     * @return the list of children.
     */
    @NotNull
    @Override
    public TreeElement[] getChildren() {
        TreeElement[] children;

        if (callbacks != null) {
            children = callbacks.toArray(new TreeElement[callbacks.size()]);
        } else {
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
        PsiElement[] fields = this.fields();
        int length = fields.length;
        String[] fieldNames = new String[length];

        for (int i = 0; i < length; i++) {
            fieldNames[i] = fields[i].getText();
        }

        return new org.elixir_lang.navigation.item_presentation.Exception(
                (org.elixir_lang.navigation.item_presentation.Module) module.getPresentation(),
                fieldNames
        );
    }

}
