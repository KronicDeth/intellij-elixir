package org.elixir_lang.structure_view.element;

import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.util.PsiTreeUtil;
import org.elixir_lang.psi.ElixirFile;
import org.elixir_lang.psi.QuoteMacro;
import org.elixir_lang.psi.call.Call;
import org.elixir_lang.structure_view.element.modular.Implementation;
import org.elixir_lang.structure_view.element.modular.Module;
import org.elixir_lang.structure_view.element.modular.Protocol;
import org.elixir_lang.structure_view.element.modular.Unknown;
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
                if (org.elixir_lang.psi.Implementation.is(call)) {
                    treeElementList.add(new Implementation(call));
                } else if (org.elixir_lang.psi.Module.is(call)) {
                    treeElementList.add(new Module(call));
                } else if (org.elixir_lang.psi.Protocol.is(call)) {
                    treeElementList.add(new Protocol(call));
                } else if (QuoteMacro.is(call)) {
                    treeElementList.add(new Quote(call));
                } else if (Unknown.is(call)) { // should always be last because it will match all macro calls
                    treeElementList.add(new Unknown(call));
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

