package org.elixir_lang.structure_view.element;

import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiElement;
import org.elixir_lang.psi.*;
import org.elixir_lang.psi.call.Call;
import org.elixir_lang.psi.impl.ElixirPsiImplUtil;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A `defexception` with its fields and the callbacks `exception/1` and `message/1` if overridden.
 */
public class Exception extends Element<Call> {
    /*
     * Fields
     */

    @Nullable
    private List<CallDefinition> callbacks = null;
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
    public void callback(@NotNull final CallDefinition callback) {
        assert callback.arity() == 1;
        assert callback.time() == Timed.Time.RUN;

        String callbackName = callback.name();
        assert callbackName.equals("exception") || callbackName.equals("message");

        if (callbacks == null) {
            callbacks = new ArrayList<CallDefinition>();
        }

        callbacks.add(callback);
    }

    /**
     * The default value elements for the struct defined for the exception.
     *
     * @return Maps the element for the key in the struct to the element in the default value.  When the list form of
     *   fields without default values is used, the Map value element is {@code  null}.
     */
    @NotNull
    public Map<PsiElement, PsiElement> defaultValueElementByKeyElement() {
        PsiElement[] finalArguments = ElixirPsiImplUtil.finalArguments(navigationItem);

        assert finalArguments != null;
        assert finalArguments.length == 1;

        PsiElement finalArgument = finalArguments[0];
        Map<PsiElement, PsiElement> defaultValueElementByKeyElement = new HashMap<PsiElement, PsiElement>(finalArguments.length);

        if (finalArgument instanceof ElixirAccessExpression) {
            ElixirAccessExpression accessExpression = (ElixirAccessExpression) finalArgument;
            PsiElement[] accessExpressionChildren = accessExpression.getChildren();

            assert accessExpressionChildren.length == 1;

            PsiElement accessExpressionChild = accessExpressionChildren[0];

            assert accessExpressionChild instanceof ElixirList;

            ElixirList list = (ElixirList) accessExpressionChild;
            PsiElement[] listChildren = list.getChildren();

            if (listChildren.length == 1) {
                PsiElement listChild = listChildren[0];

                if (listChild instanceof QuotableKeywordList) {
                    QuotableKeywordList quotableKeywordList = (QuotableKeywordList) listChild;

                    putQuotableKeywordList(defaultValueElementByKeyElement, quotableKeywordList);
                } else {
                    defaultValueElementByKeyElement.put(listChild, null);
                }
            } else {
                for (PsiElement key : list.getChildren()) {
                    defaultValueElementByKeyElement.put(key, null);
                }
            }
        } else if (finalArgument instanceof QuotableKeywordList) {
            QuotableKeywordList quotableKeywordList = (QuotableKeywordList) finalArgument;

            putQuotableKeywordList(defaultValueElementByKeyElement, quotableKeywordList);
        } else {
            assert finalArgument != null;
        }

        return defaultValueElementByKeyElement;
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
        return new org.elixir_lang.navigation.item_presentation.Exception(
                (org.elixir_lang.navigation.item_presentation.Module) module.getPresentation(),
                defaultValueElementByKeyElement()
        );
    }

    private void putQuotableKeywordList(Map<PsiElement, PsiElement> defaultValueElementByKeyElement,
                                        QuotableKeywordList quotableKeywordList) {
        for (QuotableKeywordPair quotableKeywordPair : quotableKeywordList.quotableKeywordPairList()) {
            PsiElement keyElement = quotableKeywordPair.getKeywordKey();
            PsiElement valueElement = quotableKeywordPair.getKeywordValue();

            defaultValueElementByKeyElement.put(keyElement, valueElement);
        }
    }
}
