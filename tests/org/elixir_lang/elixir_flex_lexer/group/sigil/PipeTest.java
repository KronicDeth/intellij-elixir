package org.elixir_lang.elixir_flex_lexer.group.sigil;

import org.elixir_lang.ElixirFlexLexer;
import org.elixir_lang.psi.ElixirTypes;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;

import static org.junit.Assert.assertEquals;

/**
 * Created by luke.imhoff on 9/2/14.
 */
public class PipeTest extends org.elixir_lang.elixir_flex_lexer.group.sigil.Test {
    @Override
    protected char promoter() {
        return '|';
    }

    @Test
    public void forwardSlash() throws IOException {
        reset("/");

        assertEquals(fragmentType(), flexLexer.advance());
        assertEquals(ElixirFlexLexer.GROUP, flexLexer.yystate());
    }

    @Test
    public void closingBrace() throws IOException {
        reset("}");

        assertEquals(fragmentType(), flexLexer.advance());
        assertEquals(ElixirFlexLexer.GROUP, flexLexer.yystate());
    }

    @Test
    public void closingBracket() throws IOException {
        reset("]");

        assertEquals(fragmentType(), flexLexer.advance());
        assertEquals(ElixirFlexLexer.GROUP, flexLexer.yystate());
    }

    @Test
    public void closingChrevon() throws IOException {
        reset(">");

        assertEquals(fragmentType(), flexLexer.advance());
        assertEquals(ElixirFlexLexer.GROUP, flexLexer.yystate());
    }

    @Test
    public void closingParenthesis() throws IOException {
        reset(")");

        assertEquals(fragmentType(), flexLexer.advance());
        assertEquals(ElixirFlexLexer.GROUP, flexLexer.yystate());
    }

    @Test
    public void eol() throws IOException {
        reset("\n");

        assertEquals(fragmentType(), flexLexer.advance());
        assertEquals(ElixirFlexLexer.GROUP, flexLexer.yystate());
    }

    @Test
    public void character() throws IOException {
        reset("a");

        assertEquals(fragmentType(), flexLexer.advance());
        assertEquals(ElixirFlexLexer.GROUP, flexLexer.yystate());
    }

    @Test
    public void pipe() throws IOException {
        reset("|");

        assertEquals(ElixirTypes.SIGIL_TERMINATOR, flexLexer.advance());
        assertEquals(initialState(), flexLexer.yystate());
    }
}
