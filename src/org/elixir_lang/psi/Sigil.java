package org.elixir_lang.psi;

import com.intellij.psi.PsiElement;

/**
 * Created by luke.imhoff on 2/16/15.
 */
public interface Sigil extends Fragmented, Parent, PsiElement, Quotable {
    String sigilDelimiter();
    char sigilName();

    ElixirSigilModifiers getSigilModifiers();
}
