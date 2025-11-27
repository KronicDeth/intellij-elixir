package org.elixir_lang.elixir_flex_lexer;

import com.intellij.psi.tree.IElementType;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Created by luke.imhoff on 9/8/14.
 */
public abstract class TokenTest extends Test {
    /*
     * Constants
     */

    /*
     * Fields
     */

    private CharSequence charSequence;
    private int lexicalState;
    private IElementType tokenType;
    private boolean consumeAll;

    /*
     * Constructors
     */

    public TokenTest() {
    }

    protected TokenTest(CharSequence charSequence, IElementType tokenType, int lexicalState) {
        this(charSequence, tokenType, lexicalState, true);
    }

    protected TokenTest(CharSequence charSequence, IElementType tokenType, int lexicalState, boolean consumeAll) {
        this.charSequence = charSequence;
        this.lexicalState = lexicalState;
        this.tokenType = tokenType;
        this.consumeAll = consumeAll;
    }

    /*
     * Methods
     */

    @org.junit.Test
    public void token() throws IOException {
        start(charSequence);

        assertEquals(tokenType, lexer.getTokenType());

        lexer.advance();

        if (consumeAll) {
            assertNull("Failure: expected all of \"" + charSequence + "\" to be consumed", lexer.getTokenType());
        } else {
            assertNotNull("Failure: expected all of \"" + charSequence + "\" not to be consumed", lexer.getTokenType());
        }

        assertEquals(lexicalState, lexer.getState());
    }
}
