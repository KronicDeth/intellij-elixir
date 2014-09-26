package org.elixir_lang.elixir_flex_lexer;

import com.intellij.psi.TokenType;
import org.elixir_lang.ElixirFlexLexer;
import org.elixir_lang.psi.ElixirTypes;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * Created by luke.imhoff on 9/16/14.
 */
public class AtomBodyTest extends org.elixir_lang.elixir_flex_lexer.Test {
    @Override
    protected void reset(CharSequence charSequence) throws IOException {
        // start to trigger ATOM_BODY state
        CharSequence fullCharSequence = ":_" + charSequence;
        super.reset(fullCharSequence);
        // consume ':'
        flexLexer.advance();
        // consume '_'
        flexLexer.advance();
    }

    @Test
    public void atSign() throws IOException {
        reset("@");

        assertEquals(ElixirTypes.ATOM_FRAGMENT, flexLexer.advance());
        assertEquals(ElixirFlexLexer.ATOM_BODY, flexLexer.yystate());
    }

    @Test
    public void digit() throws IOException {
        reset("0");

        assertEquals(ElixirTypes.ATOM_FRAGMENT, flexLexer.advance());
        assertEquals(ElixirFlexLexer.ATOM_BODY, flexLexer.yystate());
    }

    @Test
    public void eol() throws IOException {
        reset("\n");

        assertEquals(ElixirTypes.EOL, flexLexer.advance());
        assertEquals(initialState(), flexLexer.yystate());
    }

    @Test
    public void exclamationPoint() throws IOException {
        reset("!");

        assertEquals(ElixirTypes.ATOM_FRAGMENT, flexLexer.advance());
        assertEquals(initialState(), flexLexer.yystate());
    }

    @Test
    public void lowerCaseLetter() throws IOException {
        reset("a");

        assertEquals(ElixirTypes.ATOM_FRAGMENT, flexLexer.advance());
        assertEquals(ElixirFlexLexer.ATOM_BODY, flexLexer.yystate());
    }

    @Test
    public void questionMark() throws IOException {
        reset("?");

        assertEquals(ElixirTypes.ATOM_FRAGMENT, flexLexer.advance());
        assertEquals(initialState(), flexLexer.yystate());
    }

    @Test
    public void space() throws IOException {
        reset(" ");

        assertEquals(TokenType.WHITE_SPACE, flexLexer.advance());
        assertEquals(initialState(), flexLexer.yystate());
    }

    @Test
    public void underscore() throws IOException {
        reset("_");

        assertEquals(ElixirTypes.ATOM_FRAGMENT, flexLexer.advance());
        assertEquals(ElixirFlexLexer.ATOM_BODY, flexLexer.yystate());
    }

    @Test
    public void upperCaseLetter() throws IOException {
        reset("A");

        assertEquals(ElixirTypes.ATOM_FRAGMENT, flexLexer.advance());
        assertEquals(ElixirFlexLexer.ATOM_BODY, flexLexer.yystate());
    }
}
