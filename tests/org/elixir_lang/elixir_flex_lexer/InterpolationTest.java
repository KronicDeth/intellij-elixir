package org.elixir_lang.elixir_flex_lexer;

import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import org.elixir_lang.ElixirFlexLexer;
import org.elixir_lang.psi.ElixirTypes;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

/**
 * Created by luke.imhoff on 9/1/14.
 */
@RunWith(Parameterized.class)
public class InterpolationTest extends org.elixir_lang.elixir_flex_lexer.Test {
    /*
     * Constants
     */

    private static final int INITIAL_STATE = ElixirFlexLexer.INTERPOLATION;

    /*
     * Constructors
     */

    public InterpolationTest(CharSequence charSequence, IElementType tokenType, int lexicalState) {
        super(charSequence, tokenType, lexicalState);
    }

    /*
     * Methods
     */

    @Override
    protected int initialState() {
        return INITIAL_STATE;
    }


    @Parameterized.Parameters(
            name = "\"{0}\" parses as {1} token and advances to state {2}"
    )
    public static Collection<Object[]> generateData() {
        return Arrays.asList(new Object[][]{
                        { ":", ElixirTypes.COLON, ElixirFlexLexer.ATOM_START },
                        { "\n", ElixirTypes.EOL, INITIAL_STATE },
                        { " ", TokenType.WHITE_SPACE, INITIAL_STATE },
                        { "\t", TokenType.WHITE_SPACE, INITIAL_STATE },
                        { "\f", TokenType.WHITE_SPACE, INITIAL_STATE },
                        { "\n \r\f", ElixirTypes.EOL, INITIAL_STATE },
                        { "#", ElixirTypes.COMMENT, INITIAL_STATE },
                        { "0b10", ElixirTypes.NUMBER, INITIAL_STATE },
                        { "0X0123456789abcdefABCDEF", ElixirTypes.NUMBER, INITIAL_STATE },
                        { "0x0123456789abcdefABCDEF", ElixirTypes.NUMBER, INITIAL_STATE },
                        { "0o01234567", ElixirTypes.NUMBER, INITIAL_STATE },
                        { "001234567", ElixirTypes.NUMBER, INITIAL_STATE },
                        { "~", ElixirTypes.TILDE, ElixirFlexLexer.SIGIL },
                        { "\"\"\"", ElixirTypes.STRING_HEREDOC_PROMOTER, ElixirFlexLexer.GROUP_HEREDOC_START },
                        { "'''", ElixirTypes.CHAR_LIST_HEREDOC_PROMOTER, ElixirFlexLexer.GROUP_HEREDOC_START },
                        { "\"", ElixirTypes.STRING_PROMOTER, ElixirFlexLexer.GROUP },
                        { "'", ElixirTypes.CHAR_LIST_PROMOTER, ElixirFlexLexer.GROUP },
                }
        );
    }

}
