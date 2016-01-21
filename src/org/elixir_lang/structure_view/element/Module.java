package org.elixir_lang.structure_view.element;

import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.NavigationItem;
import com.intellij.psi.util.PsiTreeUtil;
import kotlin.NotImplementedError;
import org.apache.commons.lang.NotImplementedException;
import org.elixir_lang.psi.ElixirDoBlock;
import org.elixir_lang.psi.ElixirFile;
import org.elixir_lang.psi.ElixirStab;
import org.elixir_lang.psi.call.Call;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Module extends Element<Call> {
    /*
     * Public Instance Methods
     */

    public static boolean is(Call call) {
        return call.isCallingMacro("Elixir.Kernel", "defmodule", 2);
    }

    /*
     * Constructors
     */

    public Module(Call call) {
        super(call);
    }

    /*
     * Public Instance Methods
     */

    @NotNull
    @Override
    public TreeElement[] getChildren() {
        ElixirDoBlock doBlock = navigationItem.getDoBlock();
        TreeElement[] children;

        if (doBlock != null) {
            ElixirStab stab = doBlock.getStab();

            Collection<Call> childCalls = PsiTreeUtil.findChildrenOfType(stab, Call.class);
            List<TreeElement> treeElementList = new ArrayList<TreeElement>(childCalls.size());

            for (Call childCall : childCalls) {
                if (Module.is(childCall)) {
                    treeElementList.add(new Module(childCall));
                }
            }

            children = treeElementList.toArray(new TreeElement[treeElementList.size()]);
        } else { // one liner version with `do:` keyword argument
            throw new NotImplementedException(
                    "Walking one-liner defmodules not implemented yet. Please open an issue " +
                            "(https://github.com/KronicDeth/intellij-elixir/issues/new) with the sample text:\n" +
                            navigationItem.getText());
        }

        return children;
    }
}

