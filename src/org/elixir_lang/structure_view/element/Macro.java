package org.elixir_lang.structure_view.element;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.util.Pair;
import org.elixir_lang.navigation.item_presentation.Parent;
import org.elixir_lang.psi.call.Call;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Macro Structure view elements don't correspond to an actual element because they group together one or more
 * {@link MacroClause} elements.  In Erlang, macros would have corresponding elements, but since Elixir has
 * separate `def` calls for each macro clause there is no macro element.
 */
public class Macro implements StructureViewTreeElement {
    /*
     * Fields
     */

    private final int arity;
    @NotNull
    private final List<MacroClause> clauses = new ArrayList<MacroClause>();
    @NotNull
    private final Module module;
    @NotNull
    private final String name;

    /*
     * Constructors
     */

    public Macro(@NotNull Module module, @NotNull String name, int arity) {
        this.arity = arity;
        this.module = module;
        this.name = name;
    }

    /*
     * Public Instance Methods
     */

    public int arity() {
        return arity;
    }

    /**
     * Adds clause to macro
     *
     * @param clause the new clause for the macro
     */
    public void clause(Call clause) {
        Pair<String, Integer> nameArity = MacroClause.nameArity(clause);

        assert nameArity.first.equals(name);
        assert nameArity.second == arity;

        clauses.add(new MacroClause(this, clause));
    }

    /**
     * Returns the clauses of the macro
     *
     * @return the list of {@link MacroClause} elements.
     */
    @NotNull
    @Override
    public TreeElement[] getChildren() {
        return clauses.toArray(new TreeElement[clauses.size()]);
    }

    /**
     * The scoping module
     *
     * @return The scoping module
     */
    @NotNull
    public Module getModule() {
        return module;
    }

    /**
     * Returns the data object (usually a PSI element) corresponding to the
     * structure view element.
     *
     * @return the data object instance.
     */
    @Override
    public Object getValue() {
        return this;
    }

    /**
     * A macro groups together one or more {@code MacroClause} elements, it can not be navigated to, only its
     * {@code MacroClause} elements.
     *
     * @return <code>false</code>
     */
    @Override
    public boolean canNavigate() {
        return false;
    }

    /**
     * Cannot navigate to source because no element.
     *
     * @return <code>false</code>
     */
    @Override
    public boolean canNavigateToSource() {
        return false;
    }

    /**
     * Returns the presentation of the tree element.
     *
     * @return the element presentation.
     */
    @NotNull
    @Override
    public ItemPresentation getPresentation() {
        return new org.elixir_lang.navigation.item_presentation.Macro(
                (Parent) module.getPresentation(),
                name,
                arity
        );
    }

    @NotNull
    public String name() {
        return name;
    }

    /**
     * Does nothing because Macros aren't elements, but groups of {@code MacroClauses}.
     *
     * @param requestFocus <code>true</code> if focus requesting is necessary
     */
    @Override
    public void navigate(@SuppressWarnings("unused") boolean requestFocus) {
        // do nothing
    }
}
