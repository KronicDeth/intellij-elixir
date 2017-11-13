package org.elixir_lang.elixir_flex_lexer;

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
    protected void start(@NotNull CharSequence charSequence) {
        // start to trigger BASE_WHOLE_NUMBER_BASE state
        CharSequence fullCharSequence = "0" + charSequence;
        super.start(fullCharSequence);
        // consume '0'
        lexer.advance();
    }

    @Parameterized.Parameters(
            name = "\"{0}\" parses as {1} token and advances to state {2}"
    )
    public static Collection<Object[]> generateData() {
        return Arrays.asList(new Object[][]{
                { "A", ElixirTypes.UNKNOWN_WHOLE_NUMBER_BASE, ElixirFlexLexer.UNKNOWN_BASE_WHOLE_NUMBER },
                { "B", ElixirTypes.OBSOLETE_BINARY_WHOLE_NUMBER_BASE, ElixirFlexLexer.BINARY_WHOLE_NUMBER },
                { "C", ElixirTypes.UNKNOWN_WHOLE_NUMBER_BASE, ElixirFlexLexer.UNKNOWN_BASE_WHOLE_NUMBER },
                { "D", ElixirTypes.UNKNOWN_WHOLE_NUMBER_BASE, ElixirFlexLexer.UNKNOWN_BASE_WHOLE_NUMBER },
                { "E", ElixirTypes.UNKNOWN_WHOLE_NUMBER_BASE, ElixirFlexLexer.UNKNOWN_BASE_WHOLE_NUMBER },
                { "F", ElixirTypes.UNKNOWN_WHOLE_NUMBER_BASE, ElixirFlexLexer.UNKNOWN_BASE_WHOLE_NUMBER },
                { "G", ElixirTypes.UNKNOWN_WHOLE_NUMBER_BASE, ElixirFlexLexer.UNKNOWN_BASE_WHOLE_NUMBER },
                { "H", ElixirTypes.UNKNOWN_WHOLE_NUMBER_BASE, ElixirFlexLexer.UNKNOWN_BASE_WHOLE_NUMBER },
                { "I", ElixirTypes.UNKNOWN_WHOLE_NUMBER_BASE, ElixirFlexLexer.UNKNOWN_BASE_WHOLE_NUMBER },
                { "J", ElixirTypes.UNKNOWN_WHOLE_NUMBER_BASE, ElixirFlexLexer.UNKNOWN_BASE_WHOLE_NUMBER },
                { "K", ElixirTypes.UNKNOWN_WHOLE_NUMBER_BASE, ElixirFlexLexer.UNKNOWN_BASE_WHOLE_NUMBER },
                { "L", ElixirTypes.UNKNOWN_WHOLE_NUMBER_BASE, ElixirFlexLexer.UNKNOWN_BASE_WHOLE_NUMBER },
                { "M", ElixirTypes.UNKNOWN_WHOLE_NUMBER_BASE, ElixirFlexLexer.UNKNOWN_BASE_WHOLE_NUMBER },
                { "N", ElixirTypes.UNKNOWN_WHOLE_NUMBER_BASE, ElixirFlexLexer.UNKNOWN_BASE_WHOLE_NUMBER },
                { "O", ElixirTypes.UNKNOWN_WHOLE_NUMBER_BASE, ElixirFlexLexer.UNKNOWN_BASE_WHOLE_NUMBER },
                { "P", ElixirTypes.UNKNOWN_WHOLE_NUMBER_BASE, ElixirFlexLexer.UNKNOWN_BASE_WHOLE_NUMBER },
                { "Q", ElixirTypes.UNKNOWN_WHOLE_NUMBER_BASE, ElixirFlexLexer.UNKNOWN_BASE_WHOLE_NUMBER },
                { "R", ElixirTypes.UNKNOWN_WHOLE_NUMBER_BASE, ElixirFlexLexer.UNKNOWN_BASE_WHOLE_NUMBER },
                { "S", ElixirTypes.UNKNOWN_WHOLE_NUMBER_BASE, ElixirFlexLexer.UNKNOWN_BASE_WHOLE_NUMBER },
                { "T", ElixirTypes.UNKNOWN_WHOLE_NUMBER_BASE, ElixirFlexLexer.UNKNOWN_BASE_WHOLE_NUMBER },
                { "U", ElixirTypes.UNKNOWN_WHOLE_NUMBER_BASE, ElixirFlexLexer.UNKNOWN_BASE_WHOLE_NUMBER },
                { "V", ElixirTypes.UNKNOWN_WHOLE_NUMBER_BASE, ElixirFlexLexer.UNKNOWN_BASE_WHOLE_NUMBER },
                { "W", ElixirTypes.UNKNOWN_WHOLE_NUMBER_BASE, ElixirFlexLexer.UNKNOWN_BASE_WHOLE_NUMBER },
                { "X", ElixirTypes.OBSOLETE_HEXADECIMAL_WHOLE_NUMBER_BASE, ElixirFlexLexer.HEXADECIMAL_WHOLE_NUMBER },
                { "Y", ElixirTypes.UNKNOWN_WHOLE_NUMBER_BASE, ElixirFlexLexer.UNKNOWN_BASE_WHOLE_NUMBER },
                { "Z", ElixirTypes.UNKNOWN_WHOLE_NUMBER_BASE, ElixirFlexLexer.UNKNOWN_BASE_WHOLE_NUMBER },
                { "a", ElixirTypes.UNKNOWN_WHOLE_NUMBER_BASE, ElixirFlexLexer.UNKNOWN_BASE_WHOLE_NUMBER },
                { "b", ElixirTypes.BINARY_WHOLE_NUMBER_BASE, ElixirFlexLexer.BINARY_WHOLE_NUMBER },
                { "c", ElixirTypes.UNKNOWN_WHOLE_NUMBER_BASE, ElixirFlexLexer.UNKNOWN_BASE_WHOLE_NUMBER },
                { "d", ElixirTypes.UNKNOWN_WHOLE_NUMBER_BASE, ElixirFlexLexer.UNKNOWN_BASE_WHOLE_NUMBER },
                { "e", ElixirTypes.UNKNOWN_WHOLE_NUMBER_BASE, ElixirFlexLexer.UNKNOWN_BASE_WHOLE_NUMBER },
                { "f", ElixirTypes.UNKNOWN_WHOLE_NUMBER_BASE, ElixirFlexLexer.UNKNOWN_BASE_WHOLE_NUMBER },
                { "g", ElixirTypes.UNKNOWN_WHOLE_NUMBER_BASE, ElixirFlexLexer.UNKNOWN_BASE_WHOLE_NUMBER },
                { "h", ElixirTypes.UNKNOWN_WHOLE_NUMBER_BASE, ElixirFlexLexer.UNKNOWN_BASE_WHOLE_NUMBER },
                { "i", ElixirTypes.UNKNOWN_WHOLE_NUMBER_BASE, ElixirFlexLexer.UNKNOWN_BASE_WHOLE_NUMBER },
                { "j", ElixirTypes.UNKNOWN_WHOLE_NUMBER_BASE, ElixirFlexLexer.UNKNOWN_BASE_WHOLE_NUMBER },
                { "k", ElixirTypes.UNKNOWN_WHOLE_NUMBER_BASE, ElixirFlexLexer.UNKNOWN_BASE_WHOLE_NUMBER },
                { "l", ElixirTypes.UNKNOWN_WHOLE_NUMBER_BASE, ElixirFlexLexer.UNKNOWN_BASE_WHOLE_NUMBER },
                { "m", ElixirTypes.UNKNOWN_WHOLE_NUMBER_BASE, ElixirFlexLexer.UNKNOWN_BASE_WHOLE_NUMBER },
                { "n", ElixirTypes.UNKNOWN_WHOLE_NUMBER_BASE, ElixirFlexLexer.UNKNOWN_BASE_WHOLE_NUMBER },
                { "o", ElixirTypes.OCTAL_WHOLE_NUMBER_BASE, ElixirFlexLexer.OCTAL_WHOLE_NUMBER },
                { "p", ElixirTypes.UNKNOWN_WHOLE_NUMBER_BASE, ElixirFlexLexer.UNKNOWN_BASE_WHOLE_NUMBER },
                { "q", ElixirTypes.UNKNOWN_WHOLE_NUMBER_BASE, ElixirFlexLexer.UNKNOWN_BASE_WHOLE_NUMBER },
                { "r", ElixirTypes.UNKNOWN_WHOLE_NUMBER_BASE, ElixirFlexLexer.UNKNOWN_BASE_WHOLE_NUMBER },
                { "s", ElixirTypes.UNKNOWN_WHOLE_NUMBER_BASE, ElixirFlexLexer.UNKNOWN_BASE_WHOLE_NUMBER },
                { "t", ElixirTypes.UNKNOWN_WHOLE_NUMBER_BASE, ElixirFlexLexer.UNKNOWN_BASE_WHOLE_NUMBER },
                { "u", ElixirTypes.UNKNOWN_WHOLE_NUMBER_BASE, ElixirFlexLexer.UNKNOWN_BASE_WHOLE_NUMBER },
                { "v", ElixirTypes.UNKNOWN_WHOLE_NUMBER_BASE, ElixirFlexLexer.UNKNOWN_BASE_WHOLE_NUMBER },
                { "w", ElixirTypes.UNKNOWN_WHOLE_NUMBER_BASE, ElixirFlexLexer.UNKNOWN_BASE_WHOLE_NUMBER },
                { "x", ElixirTypes.HEXADECIMAL_WHOLE_NUMBER_BASE, ElixirFlexLexer.HEXADECIMAL_WHOLE_NUMBER },
                { "y", ElixirTypes.UNKNOWN_WHOLE_NUMBER_BASE, ElixirFlexLexer.UNKNOWN_BASE_WHOLE_NUMBER },
                { "z", ElixirTypes.UNKNOWN_WHOLE_NUMBER_BASE, ElixirFlexLexer.UNKNOWN_BASE_WHOLE_NUMBER }
        });
    }
}
