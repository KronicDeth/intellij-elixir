package org.elixir_lang.psi.call;

import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An maybe exported call definition in a source or compiled module
 */
public interface MaybeExported extends PsiElement {
    int UNEXPORTED_ARITY = -1;

    /**
     * The arity of the function or macro that was exported into the compiled .beam file OR the name of {@code def} or
     * {@code defmacro} in the decompiled source.
     *
     * @return {@link #UNEXPORTED_ARITY} if {@link #isExported()} is {@code false} OR the element comes from normal
     *   source code and the {@link org.elixir_lang.structure_view.element.CallDefinitionClause#nameArityRange(Call)}
     *   has an actual range for the arity range.
     */
    int exportedArity(@NotNull ResolveState state);

    /**
     * The name that was exported into the compiled .beam file OR the name of {@code def} or {@code defmacro} in the
     * decompiled source.
     *
     * @return {@code null} if {@link #isExported()} is {@code false}.
     */
    @Nullable
    String exportedName();

    /**
     *
     * @return {@code true} if {@code def} or {@code defmacro}
     */
    boolean isExported();
}
