package org.elixir_lang.elixir_flex_lexer.group.quote;

import com.intellij.psi.tree.IElementType;
import org.elixir_lang.ElixirFlexLexer;
import org.elixir_lang.elixir_flex_lexer.group.GroupTest;
import org.junit.Before;

import java.io.IOException;
import java.io.Reader;

import static org.junit.Assert.assertEquals;

/**
 * Created by luke.imhoff on 9/6/14.
 */
public abstract class Test extends GroupTest {
    @org.junit.Test
    public void forwardSlash() throws IOException {
        reset("/");

        assertEquals(fragmentType(), flexLexer.advance());
        assertEquals(ElixirFlexLexer.GROUP, flexLexer.yystate());
    }

    @org.junit.Test
    public void closingBrace() throws IOException {
        reset("}");

        assertEquals(fragmentType(), flexLexer.advance());
        assertEquals(ElixirFlexLexer.GROUP, flexLexer.yystate());
    }

    @org.junit.Test
    public void closingBracket() throws IOException {
        reset("]");

        assertEquals(fragmentType(), flexLexer.advance());
        assertEquals(ElixirFlexLexer.GROUP, flexLexer.yystate());
    }

    @org.junit.Test
    public void closingChrevon() throws IOException {
        reset(">");

        assertEquals(fragmentType(), flexLexer.advance());
        assertEquals(ElixirFlexLexer.GROUP, flexLexer.yystate());
    }

    @org.junit.Test
    public void closingParenthesis() throws IOException {
        reset(")");

        assertEquals(fragmentType(), flexLexer.advance());
        assertEquals(ElixirFlexLexer.GROUP, flexLexer.yystate());
    }

    @org.junit.Test
    public void eol() throws IOException {
        reset("\n");

        assertEquals(fragmentType(), flexLexer.advance());
        assertEquals(ElixirFlexLexer.GROUP, flexLexer.yystate());
    }

    @org.junit.Test
    public void character() throws IOException {
        reset("a");

        assertEquals(fragmentType(), flexLexer.advance());
        assertEquals(ElixirFlexLexer.GROUP, flexLexer.yystate());
    }

    @org.junit.Test
    public void pipe() throws IOException {
        reset("|");

        assertEquals(fragmentType(), flexLexer.advance());
        assertEquals(ElixirFlexLexer.GROUP, flexLexer.yystate());
    }
}
