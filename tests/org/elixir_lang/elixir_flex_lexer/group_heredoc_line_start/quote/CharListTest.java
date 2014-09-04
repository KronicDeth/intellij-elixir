package org.elixir_lang.elixir_flex_lexer.group_heredoc_line_start.quote;

import com.intellij.psi.TokenType;
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
public class CharListTest {
    private ElixirFlexLexer flexLexer;
    private int initialState = ElixirFlexLexer.BODY;

    private void reset(CharSequence charSequence) throws IOException {
        // start to trigger GROUP state
        CharSequence fullCharSequence = "'''\n" + charSequence;
        flexLexer.reset(fullCharSequence, 0, fullCharSequence.length(), initialState);
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
    public void spaceTripleSingleQuotes() throws IOException {
        reset(" '''");

        assertEquals(TokenType.WHITE_SPACE, flexLexer.advance());
        assertEquals(ElixirFlexLexer.GROUP_HEREDOC_END, flexLexer.yystate());
    }

    @Test
    public void tabTripleSingleQuotes() throws IOException {
       reset("\t'''");

        assertEquals(TokenType.WHITE_SPACE, flexLexer.advance());
        assertEquals(ElixirFlexLexer.GROUP_HEREDOC_END, flexLexer.yystate());
    }

    @Test
    public void formFeedTripleSingleQuotes() throws IOException {
        reset("\f'''");

        assertEquals(TokenType.WHITE_SPACE, flexLexer.advance());
        assertEquals(ElixirFlexLexer.GROUP_HEREDOC_END, flexLexer.yystate());
    }

    @Test
    public void spaceTripleDoubleQuotes() throws IOException {
        reset(" \"\"\"");

        assertEquals(TokenType.WHITE_SPACE, flexLexer.advance());
        assertEquals(ElixirFlexLexer.GROUP_HEREDOC_END, flexLexer.yystate());
    }

    @Test
    public void tabTripleDoubleQuotes() throws IOException {
        reset("\t\"\"\"");

        assertEquals(TokenType.WHITE_SPACE, flexLexer.advance());
        assertEquals(ElixirFlexLexer.GROUP_HEREDOC_END, flexLexer.yystate());
    }

    @Test
    public void formFeedTripleDoubleQuotes() throws IOException {
        reset("\f\"\"\"");

        assertEquals(TokenType.WHITE_SPACE, flexLexer.advance());
        assertEquals(ElixirFlexLexer.GROUP_HEREDOC_END, flexLexer.yystate());
    }

    @Test
    public void tripleSingleQuotes() throws IOException {
        reset("'''");

        assertEquals(ElixirTypes.CHAR_LIST_HEREDOC_TERMINATOR, flexLexer.advance());
        assertEquals(initialState, flexLexer.yystate());
    }

    @Test
    public void tripleDoubleQuotes() throws IOException {
        reset("\"\"\"");

        assertEquals(ElixirTypes.CHAR_LIST_FRAGMENT, flexLexer.advance());
        assertEquals(ElixirFlexLexer.GROUP_HEREDOC_LINE_BODY, flexLexer.yystate());
    }

    @Test
    public void eol() throws IOException {
        reset("\n");

        assertEquals(ElixirTypes.CHAR_LIST_FRAGMENT, flexLexer.advance());
        assertEquals(ElixirFlexLexer.GROUP_HEREDOC_LINE_START, flexLexer.yystate());
    }

    @Test
    public void character() throws IOException {
        reset("a");

        assertEquals(ElixirTypes.CHAR_LIST_FRAGMENT, flexLexer.advance());
        assertEquals(ElixirFlexLexer.GROUP_HEREDOC_LINE_BODY, flexLexer.yystate());
    }
}
