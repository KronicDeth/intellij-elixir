package org.elixir_lang.elixir_flex_lexer.named_sigil;

import com.intellij.psi.tree.IElementType;
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
public abstract class PromoterTest {
    private ElixirFlexLexer flexLexer;

    private void reset(CharSequence charSequence) throws IOException {
        // start to trigger NAMED_SIGIL state
        CharSequence fullCharSequence = "~" + sigilName() + charSequence;
        flexLexer.reset(fullCharSequence, 0, fullCharSequence.length(), ElixirFlexLexer.BODY);
        // consume '~'
        flexLexer.advance();
        // consume sigil name
        flexLexer.advance();
    }

    protected abstract IElementType heredocPromoterType();
    protected abstract IElementType promoterType();
    protected abstract char sigilName();

    @Before
    public void setUp() {
        flexLexer = new ElixirFlexLexer((Reader) null);
    }

    @Test
    public void tripleDoubleQuotes() throws IOException {
        reset("\"\"\"");

        assertEquals(heredocPromoterType(), flexLexer.advance());
        assertEquals(ElixirFlexLexer.GROUP_HEREDOC_START, flexLexer.yystate());
    }

    @Test
    public void tripleSingleQuotes() throws IOException {
        reset("''''");

        assertEquals(heredocPromoterType(), flexLexer.advance());
        assertEquals(ElixirFlexLexer.GROUP_HEREDOC_START, flexLexer.yystate());
    }

    @Test
    public void forwardSlash() throws IOException {
        reset("/");

        assertEquals(promoterType(), flexLexer.advance());
        assertEquals(ElixirFlexLexer.GROUP, flexLexer.yystate());
    }

    @Test
    public void pipe() throws IOException {
        reset("|");

        assertEquals(promoterType(), flexLexer.advance());
        assertEquals(ElixirFlexLexer.GROUP, flexLexer.yystate());
    }

    @Test
    public void openingBrace() throws IOException {
        reset("{");

        assertEquals(promoterType(), flexLexer.advance());
        assertEquals(ElixirFlexLexer.GROUP, flexLexer.yystate());
    }

    @Test
    public void openingBracket() throws IOException {
        reset("[");

        assertEquals(promoterType(), flexLexer.advance());
        assertEquals(ElixirFlexLexer.GROUP, flexLexer.yystate());
    }

    @Test
    public void openingChevron() throws IOException {
        reset("<");

        assertEquals(promoterType(), flexLexer.advance());
        assertEquals(ElixirFlexLexer.GROUP, flexLexer.yystate());
    }

    @Test
    public void openingDoubleQuotes() throws IOException {
        reset("\"");

        assertEquals(promoterType(), flexLexer.advance());
        assertEquals(ElixirFlexLexer.GROUP, flexLexer.yystate());
    }

    @Test
    public void openingParenthesis() throws IOException {
        reset("(");

        assertEquals(promoterType(), flexLexer.advance());
        assertEquals(ElixirFlexLexer.GROUP, flexLexer.yystate());
    }

    @Test
    public void singleQuote() throws IOException {
        reset("'");

        assertEquals(promoterType(), flexLexer.advance());
        assertEquals(ElixirFlexLexer.GROUP, flexLexer.yystate());
    }
}
