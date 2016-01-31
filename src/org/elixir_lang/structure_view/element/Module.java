package org.elixir_lang.structure_view.element;

import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.apache.commons.lang.NotImplementedException;
import org.elixir_lang.psi.ElixirDoBlock;
import org.elixir_lang.psi.ElixirStab;
import org.elixir_lang.psi.ElixirStabBody;
import org.elixir_lang.psi.call.Call;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class Module extends Element<Call> {
    /*
     * Fields
     */

    @Nullable
    private final Module parent;

    /*
     * Public Instance Methods
     */

    public static boolean is(Call call) {
        return call.isCallingMacro("Elixir.Kernel", "defmodule", 2);
    }

    /*
     * Constructors
     */

    public Module(@NotNull Call call) {
        this(null, call);
    }

    /**
     *
     * @param parent the parent {@link Module} that scopes {@code call}.
     * @param call the {@code Kernel.defmodule/2} call nested in {@code parent}.
     */
    public Module(@Nullable Module parent, @NotNull Call call) {
        super(call);
        this.parent = parent;
    }

    /*
     * Public Instance Methods
     */

    @NotNull
    @Override
    public TreeElement[] getChildren() {
        ElixirDoBlock doBlock = navigationItem.getDoBlock();
        TreeElement[] children = null;

        if (doBlock != null) {
            ElixirStab stab = doBlock.getStab();

            PsiElement[] stabChildren = stab.getChildren();

            if (stabChildren.length == 1) {
                PsiElement stabChild = stabChildren[0];

                if (stabChild instanceof ElixirStabBody) {
                    ElixirStabBody stabBody = (ElixirStabBody) stabChild;

                    Call[] childCalls = PsiTreeUtil.getChildrenOfType(stabBody, Call.class);

                    if (childCalls != null) {
                        int length = childCalls.length;
                        List<TreeElement> treeElementList = new ArrayList<TreeElement>(length);
                        Map<Pair<String, Integer>, Function> functionByNameArity = new HashMap<Pair<String, Integer>, Function>(length);
                        org.elixir_lang.structure_view.element.Exception exception = null;

                        for (Call childCall : childCalls) {
                            if (Delegation.is(childCall)) {
                                treeElementList.add(new Delegation(this, childCall));
                            } else if (org.elixir_lang.structure_view.element.Exception.is(childCall)) {
                                exception = new org.elixir_lang.structure_view.element.Exception(this, childCall);
                                treeElementList.add(exception);
                            } else if (FunctionClause.is(childCall)) {
                                Pair<String, Integer> nameArity = FunctionClause.nameArity(childCall);

                                Function function = functionByNameArity.get(nameArity);

                                if (function == null) {
                                    function = new Function(this, nameArity.first, nameArity.second);
                                    functionByNameArity.put(nameArity, function);

                                    // callbacks are nested under the behavior they are for
                                    if (exception != null && Exception.isCallback(nameArity)) {
                                        exception.callback(function);
                                    } else {
                                        treeElementList.add(function);
                                    }
                                }

                                function.clause(childCall);
                            } else if (Module.is(childCall)) {
                                treeElementList.add(new Module(this, childCall));
                            }
                        }

                        children = treeElementList.toArray(new TreeElement[treeElementList.size()]);
                    }
                }
            }
        } else {
            throw new NotImplementedException(
                    "Walking one-liner defmodules not implemented yet. Please open an issue " +
                            "(https://github.com/KronicDeth/intellij-elixir/issues/new) with the sample text:\n" +
                            navigationItem.getText());
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
        org.elixir_lang.navigation.item_presentation.Module parentPresentation = null;

        if (parent != null) {
            parentPresentation = (org.elixir_lang.navigation.item_presentation.Module) parent.getPresentation();
        }

        return new org.elixir_lang.navigation.item_presentation.Module(parentPresentation, navigationItem);
    }

}

