package org.elixir_lang.elixir_flex_lexer.sigil_modifiers.group_heredoc_end;

import com.intellij.psi.tree.IElementType;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 * Created by luke.imhoff on 10/23/14.
 */
@RunWith(Parameterized.class)
public class DoubleQuotesTest extends Test {
    /*
     * Constructors
     */

    public DoubleQuotesTest(CharSequence charSequence, IElementType tokenType, int lexicalState) {
        super(charSequence, tokenType, lexicalState);
    }

    /*
     * Methods
     */

    @Override
    protected CharSequence quotes() {
        return "\"\"\"";
    }
}
