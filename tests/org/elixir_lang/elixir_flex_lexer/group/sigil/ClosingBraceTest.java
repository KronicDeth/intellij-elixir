package org.elixir_lang.elixir_flex_lexer.group.sigil;

import com.intellij.psi.tree.IElementType;
import org.elixir_lang.psi.ElixirTypes;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

/**
 * Created by luke.imhoff on 9/2/14.
 */
@RunWith(Parameterized.class)
public class ClosingBraceTest extends Test {
    /*
     * Constructors
     */

    public ClosingBraceTest(CharSequence charSequence, IElementType tokenType, int lexicalState) {
        super(charSequence, tokenType, lexicalState);
    }

    /*
     * Methods
     */

    protected char promoter() {
        return '{';
    }


    @Parameterized.Parameters(
            name = "\"{0}\" parses as {1} token and advances to state {2}"
    )
    public static Collection<Object[]> generateData() {
        return Test.generateData(
                Arrays.asList(
                        new Object[][] {
                                { ")", FRAGMENT_TYPE, LEXICAL_STATE },
                                { "/", FRAGMENT_TYPE, LEXICAL_STATE },
                                { ";", FRAGMENT_TYPE, LEXICAL_STATE },
                                { ">", FRAGMENT_TYPE, LEXICAL_STATE },
                                { "\n", FRAGMENT_TYPE, LEXICAL_STATE },
                                { "\r\n", FRAGMENT_TYPE, LEXICAL_STATE },
                                { "]", FRAGMENT_TYPE, LEXICAL_STATE },
                                { "a", FRAGMENT_TYPE, LEXICAL_STATE },
                                { "|", FRAGMENT_TYPE, LEXICAL_STATE },
                                { "}", ElixirTypes.SIGIL_TERMINATOR, INITIAL_STATE }
                        }
                )
        );
    }
}
