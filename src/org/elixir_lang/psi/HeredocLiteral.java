package org.elixir_lang.psi;

import com.intellij.psi.PsiElement;

import java.util.List;

/**
 * Created by luke.imhoff on 1/19/15.
 */
public interface HeredocLiteral extends Parent, PsiElement, Quotable {
    ElixirHeredocPrefix getHeredocPrefix();

    List<? extends HeredocLineable> getHeredocLineList();
}
