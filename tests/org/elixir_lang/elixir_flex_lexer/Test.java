package org.elixir_lang.elixir_flex_lexer;

import org.elixir_lang.ElixirFlexLexer;
import org.junit.Before;

import java.io.IOException;
import java.io.Reader;

/**
 * Created by luke.imhoff on 9/8/14.
 */
public class Test {
    protected ElixirFlexLexer flexLexer;

    protected int initialState() {
        return ElixirFlexLexer.BODY;
    }

    protected void reset(CharSequence charSequence) throws IOException {
        reset(charSequence, initialState());
    }

    protected void reset(CharSequence charSequence, int initialState) throws IOException {
        flexLexer.reset(charSequence, 0, charSequence.length(), initialState);
    }

    @Before
    public void setUp() {
        flexLexer = new ElixirFlexLexer((Reader) null);
    }
}
