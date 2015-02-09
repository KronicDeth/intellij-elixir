package org.elixir_lang.elixir_flex_lexer.group_heredoc_end.sigil.regex;

import com.intellij.psi.tree.IElementType;
import org.junit.Ignore;

/**
 * Created by luke.imhoff on 9/12/14.
 */
@Ignore("abstract")
public abstract class Test extends org.elixir_lang.elixir_flex_lexer.group_heredoc_end.sigil.Test {
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
    protected char sigilName() {
        return 'r';
    }
}
