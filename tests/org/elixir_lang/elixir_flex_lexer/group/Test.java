package org.elixir_lang.elixir_flex_lexer.group;

import com.intellij.psi.tree.IElementType;
import org.elixir_lang.elixir_flex_lexer.TokenTest;

import java.io.IOException;

/**
 * Created by luke.imhoff on 9/6/14.
 */
public abstract class Test extends TokenTest {
    /*
     * Constructors
     */

    public Test(CharSequence charSequence, IElementType tokenType, int lexicalState) {
        super(charSequence, tokenType, lexicalState);
    }

    /*
     * Methods
     */

    protected void reset(CharSequence charSequence) throws IOException {
        super.reset(charSequence);
        flexLexer.advance();
    }
}
