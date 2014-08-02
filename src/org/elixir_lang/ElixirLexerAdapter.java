package org.elixir_lang;

import com.intellij.lexer.FlexAdapter;

/**
 * Created by luke.imhoff on 8/2/14.
 */
public class ElixirLexerAdapter extends FlexAdapter {
    public ElixirLexerAdapter() {
        super(new ElixirLexer());
    }
}
