package org.elixir_lang.elixir_flex_lexer;

import com.intellij.psi.tree.IElementType;
import org.elixir_lang.ElixirFlexLexer;
import org.elixir_lang.psi.ElixirTypes;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by luke.imhoff on 9/4/14.
 */
@RunWith(Parameterized.class)
public class SigilTest extends org.elixir_lang.elixir_flex_lexer.Test {
    /*
     * Constants
     */

    private static final int FINAL_STATE = ElixirFlexLexer.NAMED_SIGIL;

    /*
     * Constructors
     */

    public SigilTest(CharSequence charSequence, IElementType tokenType) {
        super(charSequence, tokenType, FINAL_STATE);
    }

    /*
     * Methods
     */

    @Override
    protected void reset(CharSequence charSequence) throws IOException {
        // start to trigger SIGIL state
        CharSequence fullCharSequence = "~" + charSequence;
        super.reset(fullCharSequence);
        // consume '~'
        flexLexer.advance();
    }

    @Parameterized.Parameters(
         name = "\"{0}\" parses as {1} token and advances to state {2}"
    )
    public static Collection<Object[]> generateData() {
        return Arrays.asList(new Object[][]{
                        { "C", ElixirTypes.LITERAL_CHAR_LIST_SIGIL_NAME },
                        { "R", ElixirTypes.LITERAL_REGEX_SIGIL_NAME },
                        { "S", ElixirTypes.LITERAL_STRING_SIGIL_NAME },
                        { "W", ElixirTypes.LITERAL_WORDS_SIGIL_NAME },
                        { "X", ElixirTypes.LITERAL_SIGIL_NAME },
                        { "c", ElixirTypes.INTERPOLATING_CHAR_LIST_SIGIL_NAME },
                        { "r", ElixirTypes.INTERPOLATING_REGEX_SIGIL_NAME },
                        { "s", ElixirTypes.INTERPOLATING_STRING_SIGIL_NAME },
                        { "w", ElixirTypes.INTERPOLATING_WORDS_SIGIL_NAME },
                        { "x", ElixirTypes.INTERPOLATING_SIGIL_NAME }
                }
        );
    }
}
