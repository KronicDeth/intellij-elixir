package org.elixir_lang.structure_view.element;

import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.NavigatablePsiElement;
import org.elixir_lang.navigation.item_presentation.Parent;
import org.elixir_lang.structure_view.element.modular.Modular;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Much like a {@link CallDefinition}, but
 *
 * 1) A call reference exists as an element in the PSI.
 * 2) The arity or name may not be fully resolved due to syntactically valid, but semantically invalid code.
 */
public class CallReference extends Element<NavigatablePsiElement> implements Timed, Visible {
    /*
     * Fields
     */

    /**
     * {@code null} only if arity is not valid integer in {@link Element#navigationItem}.
     */
    @Nullable
    private final Integer arity;
    @NotNull
    private final Modular modular;
    @NotNull
    private final String name;
    @NotNull
    private final Timed.Time time;

    /*
     * Constructors
     */

    public CallReference(@NotNull Modular modular, @NotNull NavigatablePsiElement psiElement, @NotNull Timed.Time time, @NotNull String name, @Nullable Integer arity) {
        super(psiElement);
        this.arity = arity;
        this.modular = modular;
        this.name = name;
        this.time = time;
    }

    /*
     * Instance Methods
     */

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

        return new org.elixir_lang.navigation.item_presentation.NameArity(
                location,
                time,
                visibility(),
                name,
                arity
        );
    }

    /**
     * Returns the list of children of the tree element.
     *
     * @return an empty list of children.
     */
    @NotNull
    @Override
    public TreeElement[] getChildren() {
        return new TreeElement[0];
    }

    /**
     * When the defined call is usable
     *
     * @return {@link Time#COMPILE} for compile time ({@code defmacro}, {@code defmacrop});
     * {@link Time#RUN} for run time {@code def}, {@code defp})
     */
    @NotNull
    @Override
    public Time time() {
        return time;
    }

    /**
     * The visibility of the element.
     *
     * @return {@link Visibility.PUBLIC}.
     */
    @Nullable
    @Override
    public Visibility visibility() {
        return Visibility.PUBLIC;
    }
}
