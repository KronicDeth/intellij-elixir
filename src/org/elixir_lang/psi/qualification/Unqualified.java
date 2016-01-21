package org.elixir_lang.psi.qualification;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.elixir_lang.psi.ElixirIdentifier;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * identifier ...
 */
public interface Unqualified extends PsiElement {
    @Contract(pure = true)
    @NotNull
    ElixirIdentifier getIdentifier();
}
