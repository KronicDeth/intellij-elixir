package org.elixir_lang.elixir_flex_lexer.group_heredoc_end.sigil;

import com.intellij.psi.tree.IElementType;
import org.elixir_lang.elixir_flex_lexer.TokenTest;
import org.junit.Ignore;

import java.io.IOException;

/**
 * Created by luke.imhoff on 9/12/14.
 */
@Ignore("abstract")
public abstract class Test extends TokenTest {
    /*
     * Constructors
     */

    public Test(CharSequence charSequence, IElementType tokenType, int lexicalState, boolean consumeAll) {
        super(charSequence, tokenType, lexicalState, consumeAll);
    }

    /*
     * Methods
     */

    @Override
    protected void start(CharSequence charSequence) {
        CharSequence fullCharSequence = sigilName() + promoter() + '\n' + charSequence;
        super.start(fullCharSequence);
        // consume sigilName
        lexer.advance();
        // consume promoter
        lexer.advance();
        // consume '\n'
        lexer.advance();
    }

    protected abstract String promoter();
    protected abstract char sigilName();
}
