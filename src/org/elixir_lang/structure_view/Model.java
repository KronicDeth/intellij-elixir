package org.elixir_lang.structure_view;

import com.intellij.ide.structureView.StructureViewModel;
import com.intellij.ide.structureView.StructureViewModelBase;
import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.structureView.TextEditorBasedStructureViewModel;
import com.intellij.ide.util.treeView.smartTree.NodeProvider;
import com.intellij.ide.util.treeView.smartTree.Sorter;
import com.intellij.openapi.editor.Editor;
import org.elixir_lang.psi.ElixirFile;
import org.elixir_lang.structure_view.element.File;
import org.elixir_lang.structure_view.node_provider.Used;
import org.elixir_lang.structure_view.sorter.Time;
import org.elixir_lang.structure_view.sorter.Visibility;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;

public class Model extends TextEditorBasedStructureViewModel implements StructureViewModel.ElementInfoProvider {
    /*
     * Static Fields
     */

    private static final Collection<NodeProvider> NODE_PROVIDERS = Arrays.<NodeProvider>asList(new Used());

    /*
     * Constructors
     */

    public Model(@NotNull ElixirFile elixirFile, @Nullable Editor editor) {
        super(editor, elixirFile);
    }

    /*
     * Public Instance Methods
     */

    @NotNull
    @Override
    public Collection<NodeProvider> getNodeProviders() {
        return NODE_PROVIDERS;
    }

    @Override
    public ElixirFile getPsiFile() {
        return (ElixirFile) super.getPsiFile();
    }

    @NotNull
    @Override
    public Sorter[] getSorters() {
       return new Sorter[] {
                Time.INSTANCE,
                Visibility.INSTANCE,
                Sorter.ALPHA_SORTER,
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

    /**
     * Returns the root element of the structure view tree.
     *
     * @return the structure view root.
     */
    @NotNull
    @Override
    public StructureViewTreeElement getRoot() {
        return new File(getPsiFile());
    }
}
