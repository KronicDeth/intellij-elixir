package org.elixir_lang.elixir_flex_lexer;

import com.intellij.psi.tree.IElementType;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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

    /*
     * Constructors
     */

    public TokenTest() {
    }

    public TokenTest(CharSequence charSequence, IElementType tokenType, int lexicalState) {
        this.charSequence = charSequence;
        this.lexicalState = lexicalState;
        this.tokenType = tokenType;
    }

    /*
     * Methods
     */

    @org.junit.Test
    public void token() throws IOException {
        reset(charSequence);

        assertEquals(tokenType, flexLexer.advance());
        assertEquals(lexicalState, flexLexer.yystate());
        assertTrue("Failure: expected all of \"" + charSequence + "\" to be consumed", flexLexer.advance() == null);
    }
}
