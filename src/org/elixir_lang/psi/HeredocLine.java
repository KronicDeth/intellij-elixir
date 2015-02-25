package org.elixir_lang.psi;

import com.intellij.psi.PsiElement;

/**
 * Created by luke.imhoff on 1/19/15.
 */
public interface HeredocLine extends PsiElement {
    public abstract ElixirHeredocLinePrefix getHeredocLinePrefix();

    public abstract Body getBody();
}
