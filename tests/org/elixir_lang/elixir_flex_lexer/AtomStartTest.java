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
    private void assertWholeAtom(CharSequence charSequence) throws IOException {
        reset(charSequence);

        assertEquals(ElixirTypes.ATOM_FRAGMENT, flexLexer.advance());
        assertEquals(initialState(), flexLexer.yystate());
    }

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
    public void percent() throws IOException {
        assertWholeAtom("%");
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

    @Test
    public void doubleLeftChevronDoubleRightChevron() throws IOException {
        assertWholeAtom("<<>>");
    }

    @Test
    public void exclamationEqualsEquals() throws IOException {
        assertWholeAtom("!==");
    }

    @Test
    public void percentBraces() throws IOException {
        assertWholeAtom("%{}");
    }

    @Test
    public void tripleAmpersand() throws IOException {
        assertWholeAtom("&&&");
    }

    @Test
    public void tripleDot() throws IOException {
        assertWholeAtom("...");
    }

    @Test
    public void tripleLeftChevron() throws IOException {
        assertWholeAtom("<<<");
    }

    @Test
    public void doubleLeftChevronTilde() throws IOException {
        assertWholeAtom("<<~");
    }

    @Test
    public void leftChevronVerticalLineRightChevron() throws IOException {
        assertWholeAtom("<|>");
    }

    @Test
    public void leftChevronTildeRightChevron() throws IOException {
        assertWholeAtom("<~>");
    }

    @Test
    public void tripleEquals() throws IOException {
        assertWholeAtom("===");
    }

    @Test
    public void tripleRightChevron() throws IOException {
        assertWholeAtom(">>>");
    }

    @Test
    public void tripleCaret() throws IOException {
        assertWholeAtom("^^^");
    }

    @Test
    public void tripleVerticalLine() throws IOException {
        assertWholeAtom("|||");
    }

    @Test
    public void tildeDoubleRightChevron() throws IOException {
        assertWholeAtom("~>>");
    }

    @Test
    public void tripleTilde() throws IOException {
        assertWholeAtom("~~~");
    }

    @Test
    public void exclamationEquals() throws IOException {
        assertWholeAtom("!=");
    }

    @Test
    public void doubleAmpersand() throws IOException {
        assertWholeAtom("&&");
    }

    @Test
    public void doublePlus() throws IOException {
        assertWholeAtom("++");
    }

    @Test
    public void doubleMinus() throws IOException {
        assertWholeAtom("--");
    }

    @Test
    public void dashRightChevron() throws IOException {
        assertWholeAtom("->");
    }

    @Test
    public void doubleColon() throws IOException {
        assertWholeAtom("::");
    }

    @Test
    public void leftChevronDash() throws IOException {
        assertWholeAtom("<-");
    }

    @Test
    public void leftChevronEqual() throws IOException {
        assertWholeAtom("<=");
    }

    @Test
    public void leftChevronRightChevron() throws IOException {
        assertWholeAtom("<>");
    }

    @Test
    public void leftChevronTilde() throws IOException {
        assertWholeAtom("<~");
    }

    @Test
    public void doubleEquals() throws IOException {
        assertWholeAtom("==");
    }

    @Test
    public void equalsTilde() throws IOException {
        assertWholeAtom("=~");
    }

    @Test
    public void rightChevronEqual() throws IOException {
        assertWholeAtom(">=");
    }

    @Test
    public void doubleBackslash() throws IOException {
        assertWholeAtom("\\\\");
    }

    @Test
    public void openBraceClosingBrace() throws IOException {
        assertWholeAtom("{}");
    }

    @Test
    public void verticalLineRightChevron() throws IOException {
        assertWholeAtom("|>");
    }

    @Test
    public void doubleVerticalLine() throws IOException {
        assertWholeAtom("||");
    }

    @Test
    public void tildeRightChevron() throws IOException {
        assertWholeAtom("~>");
    }

    @Test
    public void exclamation() throws IOException {
        assertWholeAtom("!");
    }

    @Test
    public void ampersand() throws IOException {
        assertWholeAtom("&");
    }

    @Test
    public void asterisks() throws IOException {
        assertWholeAtom("*");
    }

    @Test
    public void plus() throws IOException {
        assertWholeAtom("+");
    }

    @Test
    public void minus() throws IOException {
        assertWholeAtom("-");
    }

    @Test
    public void dot() throws IOException {
        assertWholeAtom(".");
    }

    @Test
    public void forwardSlash() throws IOException {
        assertWholeAtom("/");
    }

    @Test
    public void leftChevron() throws IOException {
        assertWholeAtom("<");
    }

    @Test
    public void equals() throws IOException {
        assertWholeAtom("=");
    }

    @Test
    public void rightChevron() throws IOException {
        assertWholeAtom(">");
    }

    @Test
    public void at() throws IOException {
        assertWholeAtom("@");
    }

    @Test
    public void caret() throws IOException {
        assertWholeAtom("^");
    }

    @Test
    public void verticalLine() throws IOException {
        assertWholeAtom("|");
    }
}
