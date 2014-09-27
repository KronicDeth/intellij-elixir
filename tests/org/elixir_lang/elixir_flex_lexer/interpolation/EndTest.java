package org.elixir_lang.elixir_flex_lexer.interpolation;

import org.elixir_lang.ElixirFlexLexer;
import org.elixir_lang.psi.ElixirTypes;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * Created by luke.imhoff on 9/27/14.
 */
public class EndTest extends org.elixir_lang.elixir_flex_lexer.Test {
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
        int lastLexicalState = ElixirFlexLexer.GROUP;
        reset("}", lastLexicalState);
        flexLexer.pushAndBegin(initialState());

        assertEquals(ElixirTypes.INTERPOLATION_END, flexLexer.advance());
        assertEquals(lastLexicalState, flexLexer.yystate());
    }
}
