package org.elixir_lang.psi;

import com.intellij.psi.PsiElement;

/**
 * Created by kadie.enheduanna.inanna on 1/7/15.
 */
public interface EscapeSequence extends PsiElement {
    int codePoint();
}
