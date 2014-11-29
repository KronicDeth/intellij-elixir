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
 * Created by luke.imhoff on 11/28/14.
 */
@RunWith(Parameterized.class)
public class BaseIntegerBaseTest extends TokenTest {
    /*
     * Constructors
     */
    public BaseIntegerBaseTest(CharSequence charSequence, IElementType tokenType, int lexicalState) {
        super(charSequence, tokenType, lexicalState);
    }

    /*
     * Methods
     */

    @Override
    protected void reset(CharSequence charSequence) throws IOException {
        // start to trigger BASE_INTEGER_BASE state
        CharSequence fullCharSequence = "0" + charSequence;
        super.reset(fullCharSequence);
        // consume '0'
        flexLexer.advance();
    }

    @Parameterized.Parameters(
            name = "\"{0}\" parses as {1} token and advances to state {2}"
    )
    public static Collection<Object[]> generateData() {
        return Arrays.asList(new Object[][]{
                { "A", ElixirTypes.UNKNOWN_INTEGER_BASE, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "B", ElixirTypes.OBSOLETE_BINARY_INTEGER_BASE, ElixirFlexLexer.BINARY_INTEGER },
                { "C", ElixirTypes.UNKNOWN_INTEGER_BASE, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "D", ElixirTypes.UNKNOWN_INTEGER_BASE, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "E", ElixirTypes.UNKNOWN_INTEGER_BASE, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "F", ElixirTypes.UNKNOWN_INTEGER_BASE, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "G", ElixirTypes.UNKNOWN_INTEGER_BASE, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "H", ElixirTypes.UNKNOWN_INTEGER_BASE, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "I", ElixirTypes.UNKNOWN_INTEGER_BASE, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "J", ElixirTypes.UNKNOWN_INTEGER_BASE, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "K", ElixirTypes.UNKNOWN_INTEGER_BASE, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "L", ElixirTypes.UNKNOWN_INTEGER_BASE, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "M", ElixirTypes.UNKNOWN_INTEGER_BASE, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "N", ElixirTypes.UNKNOWN_INTEGER_BASE, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "O", ElixirTypes.UNKNOWN_INTEGER_BASE, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "P", ElixirTypes.UNKNOWN_INTEGER_BASE, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "Q", ElixirTypes.UNKNOWN_INTEGER_BASE, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "R", ElixirTypes.UNKNOWN_INTEGER_BASE, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "S", ElixirTypes.UNKNOWN_INTEGER_BASE, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "T", ElixirTypes.UNKNOWN_INTEGER_BASE, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "U", ElixirTypes.UNKNOWN_INTEGER_BASE, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "V", ElixirTypes.UNKNOWN_INTEGER_BASE, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "W", ElixirTypes.UNKNOWN_INTEGER_BASE, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "X", ElixirTypes.UNKNOWN_INTEGER_BASE, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "Y", ElixirTypes.UNKNOWN_INTEGER_BASE, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "Z", ElixirTypes.UNKNOWN_INTEGER_BASE, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "a", ElixirTypes.UNKNOWN_INTEGER_BASE, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "b", ElixirTypes.BINARY_INTEGER_BASE, ElixirFlexLexer.BINARY_INTEGER },
                { "c", ElixirTypes.UNKNOWN_INTEGER_BASE, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "d", ElixirTypes.UNKNOWN_INTEGER_BASE, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "e", ElixirTypes.UNKNOWN_INTEGER_BASE, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "f", ElixirTypes.UNKNOWN_INTEGER_BASE, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "g", ElixirTypes.UNKNOWN_INTEGER_BASE, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "h", ElixirTypes.UNKNOWN_INTEGER_BASE, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "i", ElixirTypes.UNKNOWN_INTEGER_BASE, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "j", ElixirTypes.UNKNOWN_INTEGER_BASE, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "k", ElixirTypes.UNKNOWN_INTEGER_BASE, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "l", ElixirTypes.UNKNOWN_INTEGER_BASE, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "m", ElixirTypes.UNKNOWN_INTEGER_BASE, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "n", ElixirTypes.UNKNOWN_INTEGER_BASE, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "o", ElixirTypes.UNKNOWN_INTEGER_BASE, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "p", ElixirTypes.UNKNOWN_INTEGER_BASE, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "q", ElixirTypes.UNKNOWN_INTEGER_BASE, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "r", ElixirTypes.UNKNOWN_INTEGER_BASE, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "s", ElixirTypes.UNKNOWN_INTEGER_BASE, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "t", ElixirTypes.UNKNOWN_INTEGER_BASE, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "u", ElixirTypes.UNKNOWN_INTEGER_BASE, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "v", ElixirTypes.UNKNOWN_INTEGER_BASE, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "w", ElixirTypes.UNKNOWN_INTEGER_BASE, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "x", ElixirTypes.HEXADECIMAL_INTEGER_BASE, ElixirFlexLexer.HEXADECIMAL_INTEGER },
                { "y", ElixirTypes.UNKNOWN_INTEGER_BASE, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "z", ElixirTypes.UNKNOWN_INTEGER_BASE, ElixirFlexLexer.UNKNOWN_BASE_INTEGER }
        });
    }
}
