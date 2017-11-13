package org.elixir_lang.elixir_flex_lexer.group;

import com.intellij.psi.tree.IElementType;
import org.elixir_lang.elixir_flex_lexer.TokenTest;
import org.jetbrains.annotations.NotNull;
import org.junit.Ignore;

/**
 * Created by luke.imhoff on 9/6/14.
 */
@Ignore("abstract")
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

    @Override
    protected void start(@NotNull CharSequence charSequence) {
        super.start(charSequence);
        lexer.advance();
    }
}
