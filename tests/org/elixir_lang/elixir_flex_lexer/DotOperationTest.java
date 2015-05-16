package org.elixir_lang.elixir_flex_lexer;

import org.elixir_lang.ElixirFlexLexer;
import org.elixir_lang.psi.ElixirTypes;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by luke.imhoff on 9/16/14.
 */
public class DotOperationTest extends Test {
    /*
     * Methods
     */

    @Override
    protected void reset(CharSequence charSequence) throws IOException {
        // start to trigger ATOM_BODY state
        CharSequence fullCharSequence = "." + charSequence;
        super.reset(fullCharSequence);
        // consume '.'
        flexLexer.advance();
    }


    /*
     * Methods
     */

    @org.junit.Test
    public void andOperatorParentheses() throws IOException {
        CharSequence charSequence = "&&(";
        reset(charSequence);

        assertEquals(ElixirTypes.AND_OPERATOR, flexLexer.advance());
        assertEquals(ElixirFlexLexer.CALL_MAYBE, flexLexer.yystate());

        assertEquals(ElixirTypes.CALL, flexLexer.advance());
        assertEquals(initialState(), flexLexer.yystate());

        assertEquals(ElixirTypes.OPENING_PARENTHESIS, flexLexer.advance());
        assertEquals(initialState(), flexLexer.yystate());

        assertTrue("Failure: expected all of \"" + charSequence + "\" to be consumed", flexLexer.advance() == null);
    }

    @org.junit.Test
    public void andOperatorParenthesis() throws IOException {
        CharSequence charSequence = "&&(";
        reset(charSequence);

        assertEquals(ElixirTypes.AND_OPERATOR, flexLexer.advance());
        assertEquals(ElixirFlexLexer.CALL_MAYBE, flexLexer.yystate());

        assertEquals(ElixirTypes.CALL, flexLexer.advance());
        assertEquals(initialState(), flexLexer.yystate());

        assertEquals(ElixirTypes.OPENING_PARENTHESIS, flexLexer.advance());
        assertEquals(initialState(), flexLexer.yystate());

        assertTrue("Failure: expected all of \"" + charSequence + "\" to be consumed", flexLexer.advance() == null);
    }

    @org.junit.Test
    public void andOperatorBracket() throws IOException {
        CharSequence charSequence = "&&[";
        reset(charSequence);

        assertEquals(ElixirTypes.AND_OPERATOR, flexLexer.advance());
        assertEquals(ElixirFlexLexer.CALL_MAYBE, flexLexer.yystate());

        assertEquals(ElixirTypes.CALL, flexLexer.advance());
        assertEquals(initialState(), flexLexer.yystate());

        assertEquals(ElixirTypes.OPENING_BRACKET, flexLexer.advance());
        assertEquals(initialState(), flexLexer.yystate());

        assertTrue("Failure: expected all of \"" + charSequence + "\" to be consumed", flexLexer.advance() == null);
    }
}
