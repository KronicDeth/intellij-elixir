package org.elixir_lang.structure_view.element.call_definition_by_name_arity;

import com.intellij.ide.util.treeView.smartTree.TreeElement;
import org.elixir_lang.structure_view.element.Timed;
import org.elixir_lang.structure_view.element.modular.Modular;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MacroByNameArity extends TreeElementList {
    /*
     * Constructors
     */

    public MacroByNameArity(int size,
                            @NotNull List<TreeElement> treeElementList,
                            @NotNull Modular modular) {
        super(size, treeElementList, modular, Timed.Time.COMPILE);
    }
}
