package org.elixir_lang.elixir_flex_lexer.group;

import org.elixir_lang.ElixirFlexLexer;
import org.junit.Before;

import java.io.IOException;
import java.io.Reader;

/**
 * Created by luke.imhoff on 9/6/14.
 */
public class Test extends org.elixir_lang.elixir_flex_lexer.Test {
    protected void reset(CharSequence charSequence) throws IOException {
        super.reset(charSequence);
        flexLexer.advance();
    }
}
