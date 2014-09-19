package org.elixir_lang.elixir_flex_lexer;

import java.io.IOException;

import com.intellij.psi.TokenType;
import org.elixir_lang.ElixirFlexLexer;
import org.elixir_lang.psi.ElixirTypes;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by luke.imhoff on 9/16/14.
 */
public class AtomStartTest extends org.elixir_lang.elixir_flex_lexer.Test {
    @Override
    protected void reset(CharSequence charSequence) throws IOException {
        // start to trigger ATOM_START state
        CharSequence fullCharSequence = ":" + charSequence;
        super.reset(fullCharSequence);
        // consume '~'
        flexLexer.advance();
    }

    @Test
    public void doubleQuotes() throws IOException {
        reset("\"");

        assertEquals(ElixirTypes.STRING_PROMOTER, flexLexer.advance());
        assertEquals(ElixirFlexLexer.GROUP, flexLexer.yystate());
    }

    @Test
    public void eol() throws IOException {
        reset("\n");

        assertEquals(TokenType.BAD_CHARACTER, flexLexer.advance());
        assertEquals(ElixirFlexLexer.ATOM_START, flexLexer.yystate());
    }

    @Test
    public void lowerCaseLetter() throws IOException {
        reset("a");

        assertEquals(ElixirTypes.ATOM_FRAGMENT, flexLexer.advance());
        assertEquals(ElixirFlexLexer.ATOM_BODY, flexLexer.yystate());
    }

    @Test
    public void singleQuote() throws IOException {
        reset("'");

        assertEquals(ElixirTypes.CHAR_LIST_PROMOTER, flexLexer.advance());
        assertEquals(ElixirFlexLexer.GROUP, flexLexer.yystate());
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
