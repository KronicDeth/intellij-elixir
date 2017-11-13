package org.elixir_lang.elixir_flex_lexer;

import com.intellij.psi.tree.IElementType;
import org.junit.Ignore;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by luke.imhoff on 9/8/14.
 */
@Ignore("abstract")
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

    public TokenTest(CharSequence charSequence, IElementType tokenType, int lexicalState) {
        this(charSequence, tokenType, lexicalState, true);
    }

    public TokenTest(CharSequence charSequence, IElementType tokenType, int lexicalState, boolean consumeAll) {
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

        lexer.advance();
        assertEquals(tokenType, lexer.getTokenType());
        assertEquals(lexicalState, lexer.getState());

        lexer.advance();

        if (consumeAll) {
            assertTrue("Failure: expected all of \"" + charSequence + "\" to be consumed", lexer.getTokenType() == null);
        } else {
            assertTrue("Failure: expected all of \"" + charSequence + "\" not to be consumed", lexer.getTokenType() != null);
        }
    }
}
