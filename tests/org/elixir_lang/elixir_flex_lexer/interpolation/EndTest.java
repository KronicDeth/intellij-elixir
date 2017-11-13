package org.elixir_lang.elixir_flex_lexer.interpolation;

import org.elixir_lang.ElixirFlexLexer;
import org.elixir_lang.elixir_flex_lexer.TokenTest;
import org.elixir_lang.psi.ElixirTypes;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * Created by luke.imhoff on 9/27/14.
 */
public class EndTest extends TokenTest {
    /*
     * Methods
     */

    @Override
    protected int initialState() {
        return ElixirFlexLexer.INTERPOLATION;
    }

    @Test
    @Override
    public void token() throws IOException {
        start("\"#{}\"");

        lexer.advance();

        assertEquals(ElixirTypes.INTERPOLATION_END, lexer.getTokenType());
        assertEquals(ElixirFlexLexer.GROUP, lexer.getState());
    }
}
