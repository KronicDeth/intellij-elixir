package org.elixir_lang.structure_view.element;

import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.NavigationItem;
import com.intellij.psi.util.PsiTreeUtil;
import org.elixir_lang.psi.ElixirFile;
import org.elixir_lang.psi.call.Call;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class File extends Element {
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
        ElixirFile file = (ElixirFile) navigationItem;

        Call[] calls = PsiTreeUtil.getChildrenOfType(
                file,
                Call.class
        );
        List<TreeElement> treeElementList = new ArrayList<TreeElement>(calls.length);

        for (Call call : calls) {
            if (Module.is(call)) {
                treeElementList.add(new Module(call));
            }
        }

        return treeElementList.toArray(new TreeElement[treeElementList.size()]);
    }
}

