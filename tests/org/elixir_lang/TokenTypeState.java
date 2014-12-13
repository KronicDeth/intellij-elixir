package org.elixir_lang;

import com.intellij.psi.tree.IElementType;

/**
 * Created by luke.imhoff on 12/8/14.
 */
public class TokenTypeState {
    public IElementType tokenType;
    public int state;

    public TokenTypeState(IElementType tokenType, int state) {
        this.tokenType = tokenType;
        this.state = state;
    }
}

