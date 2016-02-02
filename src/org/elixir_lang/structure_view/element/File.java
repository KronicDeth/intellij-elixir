package org.elixir_lang.structure_view.element;

import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.util.PsiTreeUtil;
import org.elixir_lang.psi.ElixirFile;
import org.elixir_lang.psi.call.Call;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class File extends Element<ElixirFile> {
    /*
     * Constructors
     */

    public File(ElixirFile file) {
        super(file);
    }

    /*
     * Public Instance Methods
     */

    @NotNull
    @Override
    public TreeElement[] getChildren() {
        Call[] calls = PsiTreeUtil.getChildrenOfType(
                navigationItem,
                Call.class
        );
        TreeElement[] children;

        if (calls != null) {
            List<TreeElement> treeElementList = new ArrayList<TreeElement>(calls.length);

            for (Call call : calls) {
                if (Implementation.is(call)) {
                    treeElementList.add(new Implementation(call));
                } else if (Module.is(call)) {
                    treeElementList.add(new Module(call));
                }
            }

            children = treeElementList.toArray(new TreeElement[treeElementList.size()]);
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
        //noinspection ConstantConditions
        return navigationItem.getPresentation();
    }
}

