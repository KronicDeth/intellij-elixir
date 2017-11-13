package org.elixir_lang.elixir_flex_lexer;

import org.elixir_lang.ElixirFlexLexer;
import org.elixir_lang.psi.ElixirTypes;
import org.jetbrains.annotations.NotNull;

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
    protected void start(@NotNull CharSequence charSequence) {
        // start to trigger ATOM_BODY state
        CharSequence fullCharSequence = "." + charSequence;
        super.start(fullCharSequence);
        // consume '.'
        lexer.advance();
    }


    /*
     * Methods
     */

    @org.junit.Test
    public void andOperatorParentheses() throws IOException {
        CharSequence charSequence = "&&(";
        start(charSequence);

        lexer.advance();

        assertEquals(ElixirTypes.AND_SYMBOL_OPERATOR, lexer.getTokenType());
        assertEquals(ElixirFlexLexer.CALL_MAYBE, lexer.getState());

        lexer.advance();

        assertEquals(ElixirTypes.CALL, lexer.getTokenType());
        assertEquals(initialState(), lexer.getState());

        lexer.advance();

        assertEquals(ElixirTypes.OPENING_PARENTHESIS, lexer.getTokenType());
        assertEquals(initialState(), lexer.getState());

        lexer.advance();

        assertTrue("Failure: expected all of \"" + charSequence + "\" to be consumed", lexer.getTokenType() == null);
    }

    @org.junit.Test
    public void andOperatorParenthesis() throws IOException {
        CharSequence charSequence = "&&(";
        start(charSequence);

        lexer.advance();

        assertEquals(ElixirTypes.AND_SYMBOL_OPERATOR, lexer.getTokenType());
        assertEquals(ElixirFlexLexer.CALL_MAYBE, lexer.getState());

        lexer.advance();

        assertEquals(ElixirTypes.CALL, lexer.getTokenType());
        assertEquals(initialState(), lexer.getState());

        lexer.advance();

        assertEquals(ElixirTypes.OPENING_PARENTHESIS, lexer.getTokenType());
        assertEquals(initialState(), lexer.getState());

        lexer.advance();

        assertTrue("Failure: expected all of \"" + charSequence + "\" to be consumed", lexer.getTokenType() == null);
    }

    @org.junit.Test
    public void andOperatorBracket() throws IOException {
        CharSequence charSequence = "&&[";
        start(charSequence);

        lexer.advance();

        assertEquals(ElixirTypes.AND_SYMBOL_OPERATOR, lexer.getTokenType());
        assertEquals(ElixirFlexLexer.CALL_MAYBE, lexer.getState());

        lexer.advance();

        assertEquals(ElixirTypes.CALL, lexer.getTokenType());
        assertEquals(initialState(), lexer.getState());

        lexer.advance();

        assertEquals(ElixirTypes.OPENING_BRACKET, lexer.getTokenType());
        assertEquals(initialState(), lexer.getState());

        lexer.advance();

        assertTrue("Failure: expected all of \"" + charSequence + "\" to be consumed", lexer.getTokenType() == null);
    }
}
