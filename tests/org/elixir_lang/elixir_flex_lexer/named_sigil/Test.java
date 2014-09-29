package org.elixir_lang.elixir_flex_lexer.named_sigil;

import com.intellij.psi.tree.IElementType;
import org.elixir_lang.ElixirFlexLexer;
import org.elixir_lang.elixir_flex_lexer.TokenTest;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * Created by luke.imhoff on 9/4/14.
 */
public abstract class Test extends TokenTest {
    @Override
    protected void reset(CharSequence charSequence) throws IOException {
        // start to trigger NAMED_SIGIL state
        CharSequence fullCharSequence = "~" + sigilName() + charSequence;
        super.reset(fullCharSequence);
        // consume '~'
        flexLexer.advance();
        // consume sigil name
        flexLexer.advance();
    }

    protected abstract IElementType heredocPromoterType();
    protected abstract IElementType promoterType();
    protected abstract char sigilName();

    @org.junit.Test
    public void tripleDoubleQuotes() throws IOException {
        reset("\"\"\"");

        assertEquals(heredocPromoterType(), flexLexer.advance());
        assertEquals(ElixirFlexLexer.GROUP_HEREDOC_START, flexLexer.yystate());
    }

    @org.junit.Test
    public void tripleSingleQuotes() throws IOException {
        reset("''''");

        assertEquals(heredocPromoterType(), flexLexer.advance());
        assertEquals(ElixirFlexLexer.GROUP_HEREDOC_START, flexLexer.yystate());
    }

    @org.junit.Test
    public void forwardSlash() throws IOException {
        reset("/");

        assertEquals(promoterType(), flexLexer.advance());
        assertEquals(ElixirFlexLexer.GROUP, flexLexer.yystate());
    }

    @org.junit.Test
    public void pipe() throws IOException {
        reset("|");

        assertEquals(promoterType(), flexLexer.advance());
        assertEquals(ElixirFlexLexer.GROUP, flexLexer.yystate());
    }

    @org.junit.Test
    public void openingBrace() throws IOException {
        reset("{");

        assertEquals(promoterType(), flexLexer.advance());
        assertEquals(ElixirFlexLexer.GROUP, flexLexer.yystate());
    }

    @org.junit.Test
    public void openingBracket() throws IOException {
        reset("[");

        assertEquals(promoterType(), flexLexer.advance());
        assertEquals(ElixirFlexLexer.GROUP, flexLexer.yystate());
    }

    @org.junit.Test
    public void openingChevron() throws IOException {
        reset("<");

        assertEquals(promoterType(), flexLexer.advance());
        assertEquals(ElixirFlexLexer.GROUP, flexLexer.yystate());
    }

    @org.junit.Test
    public void openingDoubleQuotes() throws IOException {
        reset("\"");

        assertEquals(promoterType(), flexLexer.advance());
        assertEquals(ElixirFlexLexer.GROUP, flexLexer.yystate());
    }

    @org.junit.Test
    public void openingParenthesis() throws IOException {
        reset("(");

        assertEquals(promoterType(), flexLexer.advance());
        assertEquals(ElixirFlexLexer.GROUP, flexLexer.yystate());
    }

    @org.junit.Test
    public void singleQuote() throws IOException {
        reset("'");

        assertEquals(promoterType(), flexLexer.advance());
        assertEquals(ElixirFlexLexer.GROUP, flexLexer.yystate());
    }
}
