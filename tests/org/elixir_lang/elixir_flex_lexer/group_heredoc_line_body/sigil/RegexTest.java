package org.elixir_lang.elixir_flex_lexer.group_heredoc_line_body.sigil;

import org.elixir_lang.ElixirFlexLexer;
import org.elixir_lang.psi.ElixirTypes;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;

import static org.junit.Assert.assertEquals;

/**
 * Created by luke.imhoff on 9/3/14.
 */
public class RegexTest {
    private ElixirFlexLexer flexLexer;

    private void reset(CharSequence charSequence) throws IOException {
        // start to trigger GROUP state
        CharSequence fullCharSequence = "~r'''\n" + charSequence;
        flexLexer.reset(fullCharSequence, 0, fullCharSequence.length(), ElixirFlexLexer.BODY);
        // consume '~'
        flexLexer.advance();
        // consume 'r'
        flexLexer.advance();
        // consume "'''"
        flexLexer.advance();
        // consume '\n'
        flexLexer.advance();
    }

    @Before
    public void setUp() {
        flexLexer = new ElixirFlexLexer((Reader) null);
    }

    @Test
    public void eol() throws IOException {
        reset("\n");

        assertEquals(ElixirTypes.REGEX_FRAGMENT, flexLexer.advance());
        assertEquals(ElixirFlexLexer.GROUP_HEREDOC_LINE_START, flexLexer.yystate());
    }

    @Test
    public void character() throws IOException {
        reset("a");

        assertEquals(ElixirTypes.REGEX_FRAGMENT, flexLexer.advance());
        assertEquals(ElixirFlexLexer.GROUP_HEREDOC_LINE_BODY, flexLexer.yystate());
    }
}
