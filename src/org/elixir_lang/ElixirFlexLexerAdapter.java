package org.elixir_lang;

import com.intellij.lexer.FlexAdapter;

import java.io.Reader;

/**
 * Created by luke.imhoff on 8/3/14.
 */
public class ElixirFlexLexerAdapter extends FlexAdapter {
    public ElixirFlexLexerAdapter() {
        super(new ElixirFlexLexer((Reader) null));
    }
}
