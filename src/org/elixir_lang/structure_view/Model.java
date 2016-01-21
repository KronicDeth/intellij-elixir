package org.elixir_lang.structure_view;

import com.intellij.ide.structureView.StructureViewModel;
import com.intellij.ide.structureView.StructureViewModelBase;
import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.util.treeView.smartTree.Sorter;
import com.intellij.psi.PsiFile;
import org.elixir_lang.psi.ElixirFile;
import org.elixir_lang.structure_view.element.Element;
import org.elixir_lang.structure_view.element.File;
import org.jetbrains.annotations.NotNull;

public class Model extends StructureViewModelBase implements StructureViewModel.ElementInfoProvider {
    /*
     * Constructors
     */

    public Model(ElixirFile elixirFile) {
        super(elixirFile, new File(elixirFile));
    }

    /*
     * Public Instance Methods
     */

    @NotNull
    @Override
    public Sorter[] getSorters() {
        return new Sorter[] {
                // TODO add visibility sorter for functions
                Sorter.ALPHA_SORTER
        };
    }

    @Override
    public boolean isAlwaysShowsPlus(StructureViewTreeElement element) {
        return false;
    }

    @Override
    public boolean isAlwaysLeaf(StructureViewTreeElement element) {
        return element instanceof ElixirFile;
    }
}
