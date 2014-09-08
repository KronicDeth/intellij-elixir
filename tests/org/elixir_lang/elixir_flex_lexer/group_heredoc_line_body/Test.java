package org.elixir_lang.elixir_flex_lexer.group_heredoc_line_body;

import org.elixir_lang.ElixirFlexLexer;
import org.junit.Before;

import java.io.IOException;
import java.io.Reader;

/**
 * Created by luke.imhoff on 9/6/14.
 */
public class Test {
    protected ElixirFlexLexer flexLexer;

    protected void reset(CharSequence charSequence) throws IOException {
        flexLexer.reset(charSequence, 0, charSequence.length(), ElixirFlexLexer.BODY);
    }

    @Before
    public void setUp() {
        flexLexer = new ElixirFlexLexer((Reader) null);
    }
}
