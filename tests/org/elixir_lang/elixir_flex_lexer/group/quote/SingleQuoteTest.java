package org.elixir_lang.elixir_flex_lexer.group.quote;

import com.intellij.psi.tree.IElementType;
import org.elixir_lang.ElixirFlexLexer;
import org.elixir_lang.psi.ElixirTypes;
import org.jetbrains.annotations.NotNull;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by luke.imhoff on 9/2/14.
 */
@RunWith(Parameterized.class)
public class SingleQuoteTest extends Test {
    /*
     * Constants
     */

    public static final IElementType FRAGMENT_TYPE = ElixirTypes.FRAGMENT;

    /*
     * Constructors
     */

    public SingleQuoteTest(CharSequence charSequence, IElementType tokenType, int lexicalState) {
        super(charSequence, tokenType, lexicalState);
    }

    /*
     * Methods
     */

   @Parameterized.Parameters(
            name = "\"{0}\" parses as {1} token and advances to state {2}"
    )
    public static Collection<Object[]> generateData() {
       return Test.generateData(
               FRAGMENT_TYPE,
               Arrays.asList(
                       new Object[][] {
                               { "'", ElixirTypes.LINE_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE },
                               { "\"", FRAGMENT_TYPE, LEXICAL_STATE }
                       }
               )
       );
    }

    @Override
    protected void start(@NotNull CharSequence charSequence) {
        // start of single quote to trigger GROUP state with terminator being single quote
        CharSequence fullCharSequence = "'" + charSequence;
        super.start(fullCharSequence);
    }
}
