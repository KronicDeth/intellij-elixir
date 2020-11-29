package org.elixir_lang.psi;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by luke.imhoff on 2/16/15.
 */
public interface Sigil extends Fragmented, Parent, PsiElement, Quotable {
    @Nullable
    Integer indentation();
    @NotNull
    String sigilDelimiter();
    char sigilName();

    ElixirSigilModifiers getSigilModifiers();
}
