package org.elixir_lang.structure_view.element;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.util.Pair;
import org.elixir_lang.navigation.item_presentation.NameArity;
import org.elixir_lang.navigation.item_presentation.Parent;
import org.elixir_lang.psi.call.Call;
import org.elixir_lang.structure_view.element.modular.Modular;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * A definition for a call: either a function or a macro
 */
public class CallDefinition implements StructureViewTreeElement, Timed, Visible {
    /*
     * Enums
     */

    /*
     * Fields
     */

    private final int arity;
    @NotNull
    private final List<CallDefinitionClause> clauses = new ArrayList<CallDefinitionClause>();
    @NotNull
    private final Modular modular;
    @NotNull
    private final String name;
    @NotNull
    private final Time time;

    /*
     * Constructors
     */

    public CallDefinition(@NotNull Modular modular, @NotNull Time time, @NotNull String name, int arity) {
        this.arity = arity;
        this.modular = modular;
        this.name = name;
        this.time = time;
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
        Pair<String, Integer> nameArity = CallDefinitionClause.nameArity(clause);

        assert nameArity.first.equals(name);
        assert nameArity.second == arity;

        clauses.add(new CallDefinitionClause(this, clause));
    }

    /**
     * Returns the clauses of the macro
     *
     * @return the list of {@link CallDefinitionClause} elements.
     */
    @NotNull
    @Override
    public TreeElement[] getChildren() {
        return clauses.toArray(new TreeElement[clauses.size()]);
    }

    /**
     * The scoping module or quote
     *
     * @return The scoping module or quote
     */
    @NotNull
    public Modular getModular() {
        return modular;
    }

    /**
     * Returns the data object (usually a PSI element) corresponding to the
     * structure view element.
     *
     * @return the data object instance.
     */
    @Override
    public Object getValue() {
        return clauses.get(0);
    }

    /**
     * A macro groups together one or more {@link CallDefinitionClause} elements, so it can navigate if it has clauses.
     *
     * @return {@code true} if {@link #clauses} size is greater than 0; otherwise, {@code false}.
     */
    @Override
    public boolean canNavigate() {
        return clauses.size() > 0;
    }

    /**
     * A macro groups together one or more {@link CallDefinitionClause} elements, so it can navigate if it has clauses.
     *
     * @return {@code true} if {@link #clauses} size is greater than 0; otherwise, {@code false}.
     */
    @Override
    public boolean canNavigateToSource() {
        return clauses.size() > 0;
    }

    /**
     * Returns the presentation of the tree element.
     *
     * @return the element presentation.
     */
    @NotNull
    @Override
    public ItemPresentation getPresentation() {
        ItemPresentation itemPresentation = modular.getPresentation();
        String location = null;

        if (itemPresentation instanceof Parent) {
            Parent parentPresentation = (Parent) itemPresentation;
            location = parentPresentation.getLocatedPresentableText();
        }

        return new NameArity(
                location,
                time,
                visibility(),
                name,
                arity
        );
    }


    @NotNull
    public String name() {
        return name;
    }

    /**
     * Navigates to first clause in {@link #clauses}.
     *
     * @param requestFocus <code>true</code> if focus requesting is necessary
     */
    @Override
    public void navigate(@SuppressWarnings("unused") boolean requestFocus) {
        if (canNavigate()) {
            clauses.get(0).navigate(requestFocus);
        }
    }

    /**
     * When the defined call is usable
     *
     * @return {@link Timed.Time#COMPILE} for compile time ({@code defmacro}, {@code defmacrop});
     *   {@link Timed.Time#RUN} for run time {@code def}, {@code defp})
     */
    @Override
    @NotNull
    public Time time() {
        return time;
    }

    /**
     * The visibility of the element.
     *
     * @return {@link Visibility.PUBLIC} for public call definitions ({@code def} and {@code defmacro});
     * {@link Visibility.PRIVATE} for private call definitions ({@code defp} and {@code defmacrop}); {@code null} for
     * a mix of visibilities, such as when a call definition has a mix of call definition clause visibilities, which
     * is invalid code, but can occur temporarily while code is being edited.
     */
    @Nullable
    @Override
    public Visibility visibility() {

        int privateCount = 0;
        int publicCount = 0;

        for (CallDefinitionClause callDefinitionClause : clauses) {
            switch (callDefinitionClause.visibility()) {
                case PRIVATE:
                    privateCount++;
                    break;
                case PUBLIC:
                    publicCount++;
                    break;
            }
        }

        Visibility callDefinitionVisibility;

        if (privateCount > 0 && publicCount == 0) {
            callDefinitionVisibility = Visibility.PRIVATE;
        } else if (privateCount == 0 && publicCount > 0) {
            callDefinitionVisibility = Visibility.PUBLIC;
        } else {
            callDefinitionVisibility = null;
        }

        return callDefinitionVisibility;
    }
}
