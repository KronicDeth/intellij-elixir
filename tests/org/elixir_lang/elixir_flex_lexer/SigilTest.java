package org.elixir_lang.elixir_flex_lexer;

import org.elixir_lang.ElixirFlexLexer;
import org.elixir_lang.psi.ElixirTypes;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;

import static org.junit.Assert.assertEquals;

/**
 * Created by luke.imhoff on 9/4/14.
 */
public class SigilTest {
    private ElixirFlexLexer flexLexer;

    private void reset(CharSequence charSequence) throws IOException {
        // start to trigger SIGIL state
        CharSequence fullCharSequence = "~" + charSequence;
        flexLexer.reset(fullCharSequence, 0, fullCharSequence.length(), ElixirFlexLexer.BODY);
        // consume '~'
        flexLexer.advance();
    }

    @Before
    public void setUp() {
        flexLexer = new ElixirFlexLexer((Reader) null);
    }

    @Test
    public void C() throws IOException {
        reset("C");

        assertEquals(ElixirTypes.LITERAL_CHAR_LIST_SIGIL_NAME, flexLexer.advance());
        assertEquals(ElixirFlexLexer.NAMED_SIGIL, flexLexer.yystate());
    }

    @Test
    public void R() throws IOException {
        reset("R");

        assertEquals(ElixirTypes.LITERAL_REGEX_SIGIL_NAME, flexLexer.advance());
        assertEquals(ElixirFlexLexer.NAMED_SIGIL, flexLexer.yystate());
    }

    @Test
    public void S() throws IOException {
        reset("S");

        assertEquals(ElixirTypes.LITERAL_STRING_SIGIL_NAME, flexLexer.advance());
        assertEquals(ElixirFlexLexer.NAMED_SIGIL, flexLexer.yystate());
    }

    @Test
    public void W() throws IOException {
        reset("W");

        assertEquals(ElixirTypes.LITERAL_WORDS_SIGIL_NAME, flexLexer.advance());
        assertEquals(ElixirFlexLexer.NAMED_SIGIL, flexLexer.yystate());
    }

    @Test
    public void upperCase() throws IOException {
        reset("X");

        assertEquals(ElixirTypes.LITERAL_SIGIL_NAME, flexLexer.advance());
        assertEquals(ElixirFlexLexer.NAMED_SIGIL, flexLexer.yystate());
    }

    @Test
    public void c() throws IOException {
        reset("c");

        assertEquals(ElixirTypes.INTERPOLATING_CHAR_LIST_SIGIL_NAME, flexLexer.advance());
        assertEquals(ElixirFlexLexer.NAMED_SIGIL, flexLexer.yystate());
    }

    @Test
    public void r() throws IOException {
        reset("r");

        assertEquals(ElixirTypes.INTERPOLATING_REGEX_SIGIL_NAME, flexLexer.advance());
        assertEquals(ElixirFlexLexer.NAMED_SIGIL, flexLexer.yystate());
    }

    @Test
    public void s() throws IOException {
        reset("s");

        assertEquals(ElixirTypes.INTERPOLATING_STRING_SIGIL_NAME, flexLexer.advance());
        assertEquals(ElixirFlexLexer.NAMED_SIGIL, flexLexer.yystate());
    }

    @Test
    public void w() throws IOException {
        reset("w");

        assertEquals(ElixirTypes.INTERPOLATING_WORDS_SIGIL_NAME, flexLexer.advance());
        assertEquals(ElixirFlexLexer.NAMED_SIGIL, flexLexer.yystate());
    }

    @Test
    public void lowerCase() throws IOException {
        reset("x");

        assertEquals(ElixirTypes.INTERPOLATING_SIGIL_NAME, flexLexer.advance());
        assertEquals(ElixirFlexLexer.NAMED_SIGIL, flexLexer.yystate());
    }
}
