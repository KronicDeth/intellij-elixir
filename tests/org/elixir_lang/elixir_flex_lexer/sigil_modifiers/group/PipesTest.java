package org.elixir_lang.elixir_flex_lexer.sigil_modifiers.group;

/**
 * Created by luke.imhoff on 10/23/14.
 */

import com.intellij.psi.tree.IElementType;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class PipesTest extends Test {
    /*
     * Constructors
     */

    public PipesTest(CharSequence charSequence, IElementType tokenType, int lexicalState) {
        super(charSequence, tokenType, lexicalState);
    }

    /*
     * Methods
     */

    @Override
    protected char promoter() {
        return '|';
    }

    @Override
    protected char terminator() {
        return '|';
    }
}
