package org.elixir_lang.elixir_flex_lexer.group_heredoc_line_body.sigil;

import com.intellij.psi.tree.IElementType;
import org.elixir_lang.psi.ElixirTypes;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;

/**
 * Created by luke.imhoff on 9/3/14.
 */
@RunWith(Parameterized.class)
public class CustomTest extends PromoterTest {
    /*
     * Constructors
     */

    public CustomTest(CharSequence charSequence, IElementType tokenType, int lexicalState) {
        super(charSequence, tokenType, lexicalState);
    }

    /*
     * Methods
     */

    @Parameterized.Parameters(
            name = "\"{0}\" parses as {1} token and advances to state {2}"
    )
    public static Collection<Object[]> generateData() {
        return PromoterTest.generateData(ElixirTypes.SIGIL_FRAGMENT);
    }

    @Override
    protected char sigilName() {
        // arbitrary character so long as it is not a defined sigil in the Elixir standard library
        return 'x';
    }
}
