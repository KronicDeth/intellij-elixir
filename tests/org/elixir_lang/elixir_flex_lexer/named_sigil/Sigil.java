package org.elixir_lang.elixir_flex_lexer.named_sigil;

import com.intellij.psi.tree.IElementType;

/**
 * Created by luke.imhoff on 10/1/14.
 */
public interface Sigil {
    public IElementType heredocPromoterType();
    public IElementType promoterType();
    public char name();
}
