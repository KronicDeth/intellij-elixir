package org.elixir_lang.structure_view.element;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.util.treeView.smartTree.SortableTreeElement;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.NavigatablePsiElement;
import org.jetbrains.annotations.NotNull;

public abstract class Element<T extends NavigatablePsiElement> implements StructureViewTreeElement {
    /*
     * Private Instance Fields
     */

    protected T navigationItem;

    /*
     * Constructors
     */

    public Element(T navigationItem) {
        this.navigationItem = navigationItem;
    }

    /*
     * Public Instance Methods
     */

    /**
     * @return <code>false</code> if navigation is not possible for any reason.
     */
    @Override
    public boolean canNavigate() {
        return navigationItem.canNavigate();
    }

    /**
     * @return <code>false</code> if navigation to source is not possible for any reason.
     * Source means some kind of editor
     */
    @Override
    public boolean canNavigateToSource() {
        return navigationItem.canNavigateToSource();
    }

    /**
     * Returns the data object (usually a PSI element) corresponding to the
     * structure view element.
     *
     * @return the data object instance.
     */
    @Override
    public Object getValue() {
        return navigationItem;
    }

    /**
     * Open editor and select/navigate to the object there if possible.
     * Just do nothing if navigation is not possible like in case of a package
     *
     * @param requestFocus <code>true</code> if focus requesting is necessary
     */
    @Override
    public void navigate(boolean requestFocus) {
        navigationItem.navigate(requestFocus);
    }
}
