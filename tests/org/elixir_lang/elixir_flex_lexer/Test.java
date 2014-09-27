package org.elixir_lang.elixir_flex_lexer;

import com.intellij.psi.tree.IElementType;
import org.elixir_lang.ElixirFlexLexer;
import org.junit.Before;

import java.io.IOException;
import java.io.Reader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by luke.imhoff on 9/8/14.
 */
public class Test {
    /*
     * Fields
     */

    private CharSequence charSequence;
    private int lexicalState;
    private IElementType tokenType;

    protected ElixirFlexLexer flexLexer;

    /*
     * Constructors
     */

    public Test() {
    }

    public Test(CharSequence charSequence, IElementType tokenType, int lexicalState) {
        this.charSequence = charSequence;
        this.lexicalState = lexicalState;
        this.tokenType = tokenType;
    }

    /*
     * Methods
     */

    protected int initialState() {
        return ElixirFlexLexer.BODY;
    }

    protected void reset(CharSequence charSequence) throws IOException {
        reset(charSequence, initialState());
    }

    protected void reset(CharSequence charSequence, int initialState) throws IOException {
        flexLexer.reset(charSequence, 0, charSequence.length(), initialState);
    }

    @Before
    public void setUp() {
        flexLexer = new ElixirFlexLexer((Reader) null);
    }

    @org.junit.Test
    public void token() throws IOException {
        reset(charSequence);

        assertEquals(tokenType, flexLexer.advance());
        assertEquals(lexicalState, flexLexer.yystate());
        assertTrue("Failure: expected all of \"" + charSequence + "\" to be consumed", flexLexer.advance() == null);
    }
}
