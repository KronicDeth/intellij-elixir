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
public class InterpolationTest extends TokenTest {
    /*
     * Constants
     */

    private static final int INITIAL_STATE = ElixirFlexLexer.INTERPOLATION;

    /*
     * Constructors
     */

    public InterpolationTest(CharSequence charSequence, IElementType tokenType, int lexicalState, boolean consumeAll) {
        super(charSequence, tokenType, lexicalState, consumeAll);
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
                        { " ", TokenType.WHITE_SPACE, INITIAL_STATE, true },
                        { "!", ElixirTypes.UNARY_OPERATOR, INITIAL_STATE, true},
                        { "#", ElixirTypes.COMMENT, INITIAL_STATE, true },
                        { "'", ElixirTypes.CHAR_LIST_PROMOTER, ElixirFlexLexer.GROUP, true },
                        { "'''", ElixirTypes.CHAR_LIST_HEREDOC_PROMOTER, ElixirFlexLexer.GROUP_HEREDOC_START, true },
                        { "+", ElixirTypes.DUAL_OPERATOR, INITIAL_STATE, true},
                        { "++", ElixirTypes.TWO_OPERATOR, INITIAL_STATE, true},
                        { "-", ElixirTypes.DUAL_OPERATOR, INITIAL_STATE, true},
                        { "--", ElixirTypes.TWO_OPERATOR, INITIAL_STATE, true},
                        { "..", ElixirTypes.TWO_OPERATOR, INITIAL_STATE, true},
                        { "001234567", ElixirTypes.NUMBER, INITIAL_STATE, true },
                        { "0X0123456789abcdefABCDEF", ElixirTypes.NUMBER, INITIAL_STATE, true },
                        { "0b10", ElixirTypes.NUMBER, INITIAL_STATE, true },
                        { "0o01234567", ElixirTypes.NUMBER, INITIAL_STATE, true },
                        { "0x0123456789abcdefABCDEF", ElixirTypes.NUMBER, INITIAL_STATE, true },
                        { ":", ElixirTypes.COLON, ElixirFlexLexer.ATOM_START, true },
                        { ";", ElixirTypes.EOL, INITIAL_STATE, true },
                        { "<", ElixirTypes.RELATIONAL_OPERATOR, INITIAL_STATE, true },
                        { "<<<", ElixirTypes.ARROW_OPERATOR, INITIAL_STATE, true },
                        { "<<~", ElixirTypes.ARROW_OPERATOR, INITIAL_STATE, true },
                        { "<=", ElixirTypes.RELATIONAL_OPERATOR, INITIAL_STATE, true },
                        { "<>", ElixirTypes.TWO_OPERATOR, INITIAL_STATE, true},
                        { "<|>", ElixirTypes.ARROW_OPERATOR, INITIAL_STATE, true },
                        { "<~", ElixirTypes.ARROW_OPERATOR, INITIAL_STATE, true },
                        { "<~>", ElixirTypes.ARROW_OPERATOR, INITIAL_STATE, true },
                        { ">", ElixirTypes.RELATIONAL_OPERATOR, INITIAL_STATE, true },
                        { ">=", ElixirTypes.RELATIONAL_OPERATOR, INITIAL_STATE, true },
                        { ">>>", ElixirTypes.ARROW_OPERATOR, INITIAL_STATE, true },
                        { "? ", ElixirTypes.CHAR_TOKEN, INITIAL_STATE, true },
                        { "?\\a", ElixirTypes.CHAR_TOKEN, INITIAL_STATE, true },
                        { "?\\xa", ElixirTypes.CHAR_TOKEN, INITIAL_STATE, true },
                        { "?\\xaB", ElixirTypes.CHAR_TOKEN, INITIAL_STATE, true },
                        { "?\\x{890aBc}", ElixirTypes.CHAR_TOKEN, INITIAL_STATE, true },
                        { "?\\x{890aB}", ElixirTypes.CHAR_TOKEN, INITIAL_STATE, true },
                        { "?\\x{890a}", ElixirTypes.CHAR_TOKEN, INITIAL_STATE, true },
                        { "?\\x{890}", ElixirTypes.CHAR_TOKEN, INITIAL_STATE, true },
                        { "?\\x{89}", ElixirTypes.CHAR_TOKEN, INITIAL_STATE, true },
                        { "?\\x{8}", ElixirTypes.CHAR_TOKEN, INITIAL_STATE, true },
                        { "\"", ElixirTypes.STRING_PROMOTER, ElixirFlexLexer.GROUP, true },
                        { "\"\"\"", ElixirTypes.STRING_HEREDOC_PROMOTER, ElixirFlexLexer.GROUP_HEREDOC_START, true },
                        { "\\;", TokenType.BAD_CHARACTER, INITIAL_STATE, false },
                        { "\\\n", TokenType.WHITE_SPACE, INITIAL_STATE, true },
                        { "\\\r\n", TokenType.WHITE_SPACE, INITIAL_STATE, true },
                        { "\f", TokenType.WHITE_SPACE, INITIAL_STATE, true },
                        { "\n", ElixirTypes.EOL, INITIAL_STATE, true },
                        { "\r\n", ElixirTypes.EOL, INITIAL_STATE, true },
                        { "\t", TokenType.WHITE_SPACE, INITIAL_STATE, true },
                        { "^", ElixirTypes.UNARY_OPERATOR, INITIAL_STATE, true},
                        { "not", ElixirTypes.UNARY_OPERATOR, INITIAL_STATE, true},
                        { "|>", ElixirTypes.ARROW_OPERATOR, INITIAL_STATE, true },
                        { "~", ElixirTypes.TILDE, ElixirFlexLexer.SIGIL, true },
                        { "~>", ElixirTypes.ARROW_OPERATOR, INITIAL_STATE, true },
                        { "~>>", ElixirTypes.ARROW_OPERATOR, INITIAL_STATE, true },
                        { "~~~", ElixirTypes.UNARY_OPERATOR, INITIAL_STATE, true}
                }
        );
    }

}
