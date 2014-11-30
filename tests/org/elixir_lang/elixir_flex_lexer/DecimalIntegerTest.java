package org.elixir_lang.elixir_flex_lexer;

import com.intellij.psi.TokenType;
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
public class DecimalIntegerTest extends TokenTest {
    /*
     * Constructors
     */
    public DecimalIntegerTest(CharSequence charSequence, IElementType tokenType, int lexicalState) {
        super(charSequence, tokenType, lexicalState);
    }

    /*
     * Methods
     */

    @Override
    protected void reset(CharSequence charSequence) throws IOException {
        // start to trigger DECIMAL_INTEGER state
        CharSequence fullCharSequence = "1" + charSequence;
        super.reset(fullCharSequence);
        // consume '0'
        flexLexer.advance();
    }

    @Parameterized.Parameters(
            name = "\"{0}\" parses as {1} token and advances to state {2}"
    )
    public static Collection<Object[]> generateData() {
        return Arrays.asList(new Object[][]{
                { " ", TokenType.WHITE_SPACE, INITIAL_STATE },
                { "!", ElixirTypes.UNARY_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE },
                { "#", ElixirTypes.COMMENT, INITIAL_STATE },
                { "$", TokenType.BAD_CHARACTER, INITIAL_STATE },
                { "%", ElixirTypes.STRUCT_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE },
                { "&", ElixirTypes.CAPTURE_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE },
                { "'", ElixirTypes.CHAR_LIST_PROMOTER, ElixirFlexLexer.GROUP },
                { "'''", ElixirTypes.CHAR_LIST_HEREDOC_PROMOTER, ElixirFlexLexer.GROUP_HEREDOC_START },
                { "(", ElixirTypes.OPENING_PARENTHESIS, INITIAL_STATE },
                { ")", ElixirTypes.CLOSING_PARENTHESIS, INITIAL_STATE },
                { "*", ElixirTypes.MULTIPLICATION_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE },
                { "+", ElixirTypes.DUAL_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE },
                { ",", ElixirTypes.COMMA, INITIAL_STATE },
                { "-", ElixirTypes.DUAL_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE },
                { ".", ElixirTypes.DOT_OPERATOR, INITIAL_STATE },
                { "/", ElixirTypes.MULTIPLICATION_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE },
                { "0", null, ElixirFlexLexer.DECIMAL_INTEGER },
                { "1", null, ElixirFlexLexer.DECIMAL_INTEGER },
                { "2", null, ElixirFlexLexer.DECIMAL_INTEGER },
                { "3", null, ElixirFlexLexer.DECIMAL_INTEGER },
                { "4", null, ElixirFlexLexer.DECIMAL_INTEGER },
                { "5", null, ElixirFlexLexer.DECIMAL_INTEGER },
                { "6", null, ElixirFlexLexer.DECIMAL_INTEGER },
                { "7", null, ElixirFlexLexer.DECIMAL_INTEGER },
                { "8", null, ElixirFlexLexer.DECIMAL_INTEGER },
                { "9", null, ElixirFlexLexer.DECIMAL_INTEGER },
                { ":", ElixirTypes.COLON, ElixirFlexLexer.ATOM_START },
                { ";", ElixirTypes.SEMICOLON, INITIAL_STATE },
                { "<", ElixirTypes.RELATIONAL_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE },
                { "=", ElixirTypes.MATCH_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE },
                { ">", ElixirTypes.RELATIONAL_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE },
                { "?", TokenType.BAD_CHARACTER, INITIAL_STATE },
                { "@", ElixirTypes.AT_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE },
                { "A", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_INTEGER },
                { "B", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_INTEGER },
                { "C", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_INTEGER },
                { "D", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_INTEGER },
                { "E", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_INTEGER },
                { "F", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_INTEGER },
                { "G", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_INTEGER },
                { "H", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_INTEGER },
                { "I", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_INTEGER },
                { "J", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_INTEGER },
                { "K", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_INTEGER },
                { "L", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_INTEGER },
                { "M", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_INTEGER },
                { "N", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_INTEGER },
                { "O", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_INTEGER },
                { "P", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_INTEGER },
                { "Q", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_INTEGER },
                { "R", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_INTEGER },
                { "S", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_INTEGER },
                { "T", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_INTEGER },
                { "U", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_INTEGER },
                { "V", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_INTEGER },
                { "W", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_INTEGER },
                { "X", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_INTEGER },
                { "Y", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_INTEGER },
                { "Z", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_INTEGER },
                { "[", ElixirTypes.OPENING_BRACKET, INITIAL_STATE },
                { "\"", ElixirTypes.STRING_PROMOTER, ElixirFlexLexer.GROUP },
                { "\"\"\"", ElixirTypes.STRING_HEREDOC_PROMOTER, ElixirFlexLexer.GROUP_HEREDOC_START },
                { "\n", ElixirTypes.EOL, INITIAL_STATE },
                { "\r\n", ElixirTypes.EOL, INITIAL_STATE },
                { "]", ElixirTypes.CLOSING_BRACKET, INITIAL_STATE },
                { "^", ElixirTypes.UNARY_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE },
                { "_", ElixirTypes.DECIMAL_SEPARATOR, ElixirFlexLexer.DECIMAL_INTEGER },
                { "`", TokenType.BAD_CHARACTER, INITIAL_STATE },
                { "a", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_INTEGER },
                { "b", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_INTEGER },
                { "c", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_INTEGER },
                { "d", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_INTEGER },
                { "e", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_INTEGER },
                { "f", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_INTEGER },
                { "g", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_INTEGER },
                { "h", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_INTEGER },
                { "i", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_INTEGER },
                { "j", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_INTEGER },
                { "k", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_INTEGER },
                { "l", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_INTEGER },
                { "m", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_INTEGER },
                { "n", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_INTEGER },
                { "o", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_INTEGER },
                { "p", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_INTEGER },
                { "q", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_INTEGER },
                { "r", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_INTEGER },
                { "s", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_INTEGER },
                { "t", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_INTEGER },
                { "u", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_INTEGER },
                { "v", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_INTEGER },
                { "w", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_INTEGER },
                { "x", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_INTEGER },
                { "y", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_INTEGER },
                { "z", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_INTEGER },
                { "{", TokenType.BAD_CHARACTER, INITIAL_STATE },
                { "|", ElixirTypes.PIPE_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE },
                { "}", TokenType.BAD_CHARACTER, INITIAL_STATE },
                { "~", ElixirTypes.TILDE, ElixirFlexLexer.SIGIL }
        });
    }
}
