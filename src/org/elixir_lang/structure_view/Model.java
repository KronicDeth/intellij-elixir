package org.elixir_lang.structure_view;

import com.intellij.ide.structureView.StructureViewModel;
import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.structureView.TextEditorBasedStructureViewModel;
import com.intellij.ide.util.treeView.smartTree.NodeProvider;
import com.intellij.ide.util.treeView.smartTree.Sorter;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import org.elixir_lang.psi.ElixirAtom;
import org.elixir_lang.psi.ElixirFile;
import org.elixir_lang.psi.QuotableKeywordPair;
import org.elixir_lang.psi.QuoteMacro;
import org.elixir_lang.psi.call.Call;
import org.elixir_lang.structure_view.element.*;
import org.elixir_lang.structure_view.element.Exception;
import org.elixir_lang.structure_view.element.modular.Implementation;
import org.elixir_lang.structure_view.element.modular.Module;
import org.elixir_lang.structure_view.element.modular.Protocol;
import org.elixir_lang.structure_view.element.modular.Unknown;
import org.elixir_lang.structure_view.element.structure.Field;
import org.elixir_lang.structure_view.element.structure.FieldWithDefaultValue;
import org.elixir_lang.structure_view.element.structure.Structure;
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
     * Static Methods
     */

    public static boolean isSuitable(Call call) {
        // everything in {@link Module#childCallTreeElements}
        return org.elixir_lang.psi.CallDefinitionClause.isFunction(call) ||
                org.elixir_lang.psi.CallDefinitionClause.isMacro(call) ||
                CallDefinitionHead.Companion.is(call) ||
                CallDefinitionSpecification.Companion.is(call) ||
                Callback.Companion.is(call) ||
                Delegation.is(call) ||
                Exception.is(call) ||
                Implementation.is(call) ||
                Module.Companion.is(call) ||
                Overridable.is(call) ||
                Protocol.is(call) ||
                QuoteMacro.is(call) ||
                Structure.is(call) ||
                Type.is(call) ||
                org.elixir_lang.psi.Use.is(call) ||
                Unknown.is(call);
    }

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

    /**
     * Returns the list of PSI element classes which are shown as structure view elements.
     * When determining the current editor element, the PSI tree is walked up until an element
     * matching one of these classes is found.
     *
     * @return the list of classes
     */
    @NotNull
    @Override
    protected Class[] getSuitableClasses() {
        return new Class[]{
                Call.class,
                ElixirAtom.class,
                QuotableKeywordPair.class
        };
    }

    @Override
    public boolean isAlwaysShowsPlus(StructureViewTreeElement element) {
        return false;
    }

    @Override
    public boolean isAlwaysLeaf(StructureViewTreeElement element) {
        return false;
    }

    @Override
    protected boolean isSuitable(PsiElement element) {
        boolean suitable = false;

        // checks if the class is good
        if (super.isSuitable(element)) {
            // calls can be nested in calls, so need to check for sure
            if (element instanceof Call) {
                Call call = (Call) element;
                suitable = isSuitable(call);
            } else if (element instanceof ElixirAtom) {
                ElixirAtom atom = (ElixirAtom) element;
                suitable = Field.is(atom);
            } else if (element instanceof QuotableKeywordPair) {
                QuotableKeywordPair quotableKeywordPair = (QuotableKeywordPair) element;
                suitable = FieldWithDefaultValue.is(quotableKeywordPair);
            }
        }

        return suitable;
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
