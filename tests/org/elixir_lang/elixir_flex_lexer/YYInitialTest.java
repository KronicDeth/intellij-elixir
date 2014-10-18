package org.elixir_lang.elixir_flex_lexer;

import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import org.elixir_lang.ElixirFlexLexer;
import org.elixir_lang.psi.ElixirTypes;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

/**
 * Created by luke.imhoff on 9/1/14.
 */
@RunWith(Parameterized.class)
public class YYInitialTest extends TokenTest {
    /*
     * Constants
     */

    private static final int INITIAL_STATE = ElixirFlexLexer.YYINITIAL;

    /*
     * Constructors
     */

    public YYInitialTest(CharSequence charSequence, IElementType tokenType, int lexicalState) {
        super(charSequence, tokenType, lexicalState);
    }

    /*
     * Methods
     */

    protected int initialState() {
        return INITIAL_STATE;
    }

    @Parameterized.Parameters(
            name = "\"{0}\" parses as {1} token and advances to state {2}"
    )
    public static Collection<Object[]> generateData() {
        return Arrays.asList(new Object[][]{
                        { " ", TokenType.WHITE_SPACE, ElixirFlexLexer.YYINITIAL },
                        { "", null, INITIAL_STATE },
                        { "#", ElixirTypes.COMMENT, ElixirFlexLexer.YYINITIAL },
                        { "'", ElixirTypes.CHAR_LIST_PROMOTER, ElixirFlexLexer.GROUP },
                        { "'''", ElixirTypes.CHAR_LIST_HEREDOC_PROMOTER, ElixirFlexLexer.GROUP_HEREDOC_START },
                        { "001234567", ElixirTypes.NUMBER, ElixirFlexLexer.YYINITIAL },
                        { "0B10", ElixirTypes.NUMBER, ElixirFlexLexer.YYINITIAL },
                        { "0X0123456789abcdefABCDEF", ElixirTypes.NUMBER, ElixirFlexLexer.YYINITIAL },
                        { "0b10", ElixirTypes.NUMBER, ElixirFlexLexer.YYINITIAL },
                        { "0o01234567", ElixirTypes.NUMBER, ElixirFlexLexer.YYINITIAL },
                        { "0x0123456789abcdefABCDEF", ElixirTypes.NUMBER, ElixirFlexLexer.YYINITIAL },
                        { ":", ElixirTypes.COLON, ElixirFlexLexer.ATOM_START },
                        { "\"", ElixirTypes.STRING_PROMOTER, ElixirFlexLexer.GROUP },
                        { "\"\"\"",ElixirTypes.STRING_HEREDOC_PROMOTER,ElixirFlexLexer.GROUP_HEREDOC_START },
                        { "\f", TokenType.WHITE_SPACE, ElixirFlexLexer.YYINITIAL },
                        { "\n", TokenType.WHITE_SPACE, ElixirFlexLexer.YYINITIAL },
                        { "\t", TokenType.WHITE_SPACE, ElixirFlexLexer.YYINITIAL },
                        { "~", ElixirTypes.TILDE, ElixirFlexLexer.SIGIL },
                }
        );
    }
}
