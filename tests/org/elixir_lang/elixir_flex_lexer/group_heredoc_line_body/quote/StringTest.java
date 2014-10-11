package org.elixir_lang.elixir_flex_lexer.group_heredoc_line_body.quote;

import com.intellij.psi.tree.IElementType;
import org.elixir_lang.psi.ElixirTypes;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;

/**
 * Created by luke.imhoff on 9/3/14.
 */
@RunWith(Parameterized.class)
public class StringTest extends PromoterTest {
    /*
     * Constructors
     */


    public StringTest(CharSequence charSequence, IElementType tokenType, int lexicalState) {
        super(charSequence, tokenType, lexicalState);
    }

    /*
     * Methods
     */

    @Parameterized.Parameters(
            name = "\"{0}\" parses as {1} token and advances to state {2}"
    )
    public static Collection<Object[]> generateData() {
        return PromoterTest.generateData(ElixirTypes.STRING_FRAGMENT);
    }

    @Override
    protected String promoter() {
        return "\"\"\"";
    }
}
