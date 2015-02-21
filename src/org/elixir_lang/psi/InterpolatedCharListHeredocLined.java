package org.elixir_lang.psi;

import com.intellij.psi.PsiElement;

import java.util.List;

/**
 * Created by luke.imhoff on 2/14/15.
 */
public interface InterpolatedCharListHeredocLined extends PsiElement {
    public List<ElixirInterpolatedCharListHeredocLine> getInterpolatedCharListHeredocLineList();
}
