package org.elixir_lang.elixir_flex_lexer;

import com.intellij.psi.TokenType;
import org.elixir_lang.ElixirFlexLexer;
import org.elixir_lang.psi.ElixirTypes;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;

import static org.junit.Assert.assertEquals;

/**
 * Created by luke.imhoff on 9/1/14.
 */
public class InterpolationTest extends org.elixir_lang.elixir_flex_lexer.Test {
    @Override
    protected int initialState() {
        return ElixirFlexLexer.INTERPOLATION;
    }

    @Test
    public void eol() throws IOException {
        reset("\n");

        assertEquals(ElixirTypes.EOL, flexLexer.advance());
        assertEquals(initialState(), flexLexer.yystate());
    }

    @Test
    public void space() throws IOException {
        reset(" ");

        assertEquals(TokenType.WHITE_SPACE, flexLexer.advance());
        assertEquals(initialState(), flexLexer.yystate());
    }

    @Test
    public void tab() throws IOException {
        reset("\t");

        assertEquals(TokenType.WHITE_SPACE, flexLexer.advance());
        assertEquals(initialState(), flexLexer.yystate());
    }

    @Test
    public void formFeed() throws IOException {
        reset("\f");

        assertEquals(TokenType.WHITE_SPACE, flexLexer.advance());
        assertEquals(initialState(), flexLexer.yystate());
    }

    @Test
    public void eolWhitespace() throws IOException {
        reset("\n \t\f");

        assertEquals(ElixirTypes.EOL, flexLexer.advance());
        assertEquals(initialState(), flexLexer.yystate());
    }

    @Test
    public void hashSymbol() throws IOException {
        reset("#");

        assertEquals(ElixirTypes.COMMENT, flexLexer.advance());
        assertEquals(initialState(), flexLexer.yystate());
    }

    @Test
    public void binaryInteger() throws IOException {
        reset("0b10");

        assertEquals(ElixirTypes.NUMBER, flexLexer.advance());
        assertEquals(initialState(), flexLexer.yystate());
    }

    @Test
    public void deprecatedBinaryInteger() throws IOException {
        reset("0B10");

        assertEquals(ElixirTypes.NUMBER, flexLexer.advance());
        assertEquals(initialState(), flexLexer.yystate());
    }

    @Test
    public void hexadecimalInteger() throws IOException {
        reset("0X0123456789abcdefABCDEF");

        assertEquals(ElixirTypes.NUMBER, flexLexer.advance());
        assertEquals(initialState(), flexLexer.yystate());
    }

    @Test
    public void deprecatedHexadecimalInteger() throws IOException {
        reset("0x0123456789abcdefABCDEF");

        assertEquals(ElixirTypes.NUMBER, flexLexer.advance());
        assertEquals(initialState(), flexLexer.yystate());
    }

    @Test
    public void octalInteger() throws IOException {
        reset("0o01234567");

        assertEquals(ElixirTypes.NUMBER, flexLexer.advance());
        assertEquals(initialState(), flexLexer.yystate());
    }

    @Test
    public void deprecatedOctalInteger() throws IOException {
        reset("001234567");

        assertEquals(ElixirTypes.NUMBER, flexLexer.advance());
        assertEquals(initialState(), flexLexer.yystate());
    }

    @Test
    public void tilde() throws IOException {
        reset("~");

        assertEquals(ElixirTypes.TILDE, flexLexer.advance());
        assertEquals(ElixirFlexLexer.SIGIL, flexLexer.yystate());
    }

    @Test
    public void tripleDoubleQuotes() throws IOException {
        reset("\"\"\"");

        assertEquals(ElixirTypes.STRING_HEREDOC_PROMOTER, flexLexer.advance());
        assertEquals(ElixirFlexLexer.GROUP_HEREDOC_START, flexLexer.yystate());
    }

    @Test
    public void tripleSingleQuotes() throws IOException {
        reset("'''");

        assertEquals(ElixirTypes.CHAR_LIST_HEREDOC_PROMOTER, flexLexer.advance());
        assertEquals(ElixirFlexLexer.GROUP_HEREDOC_START, flexLexer.yystate());
    }

    @Test
    public void doubleQuotes() throws IOException {
        reset("\"");

        assertEquals(ElixirTypes.STRING_PROMOTER, flexLexer.advance());
        assertEquals(ElixirFlexLexer.GROUP, flexLexer.yystate());
    }

    @Test
    public void singleQuotes() throws IOException {
        reset("'");

        assertEquals(ElixirTypes.CHAR_LIST_PROMOTER, flexLexer.advance());
        assertEquals(ElixirFlexLexer.GROUP, flexLexer.yystate());
    }

    @Test
    public void closingBrace() throws IOException {
        int lastLexicalState = ElixirFlexLexer.GROUP;
        reset("}", lastLexicalState);
        flexLexer.pushAndBegin(initialState());

        assertEquals(ElixirTypes.INTERPOLATION_END, flexLexer.advance());
        assertEquals(lastLexicalState, flexLexer.yystate());
    }
}
