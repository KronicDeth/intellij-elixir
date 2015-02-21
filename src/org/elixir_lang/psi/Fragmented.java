package org.elixir_lang.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;

/**
 * Created by luke.imhoff on 2/4/15.
 */
public interface Fragmented extends PsiElement {
    IElementType getFragmentType();
}
