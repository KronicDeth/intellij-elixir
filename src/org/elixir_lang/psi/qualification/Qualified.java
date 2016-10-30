package org.elixir_lang.psi.qualification;

import com.intellij.psi.PsiElement;
import org.elixir_lang.psi.ElixirRelativeIdentifier;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * <expression> dotInfixOperator relativeIdentifier ...
 */
public interface Qualified extends PsiElement {
    @Contract(pure = true)
    @NotNull
    PsiElement qualifier();

    @Contract(pure = true)
    @NotNull
    ElixirRelativeIdentifier getRelativeIdentifier();
}
