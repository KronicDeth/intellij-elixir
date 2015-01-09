package org.elixir_lang.psi;

import com.intellij.psi.PsiElement;

/**
 * Created by luke.imhoff on 1/7/15.
 */
public interface EscapeSequence extends PsiElement {
    public int codePoint();
}
