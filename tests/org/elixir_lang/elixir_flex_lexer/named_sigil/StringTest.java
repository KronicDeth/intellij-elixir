package org.elixir_lang.elixir_flex_lexer.named_sigil;

import com.intellij.psi.tree.IElementType;
import org.elixir_lang.psi.ElixirTypes;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;

/**
 * Created by luke.imhoff on 9/4/14.
 */
@RunWith(Parameterized.class)
public class StringTest extends Test {
    @Override
    public char name() {
        return 's';
    }

    /*
     * Constructors
     */

    public StringTest(CharSequence charSequence, IElementType tokenType, int lexicalState) {
        super(charSequence, tokenType, lexicalState);
    }

    @Parameterized.Parameters(
            name = "\"{0}\" parses as {1} token and advances to state {2}"
    )
    public static Collection<Object[]> generateData() {
        return Test.generateData();
    }
}
