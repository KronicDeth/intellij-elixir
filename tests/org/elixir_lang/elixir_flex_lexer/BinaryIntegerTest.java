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
public class BinaryIntegerTest extends TokenTest {
    /*
     * Constructors
     */
    public BinaryIntegerTest(CharSequence charSequence, IElementType tokenType, int lexicalState) {
        super(charSequence, tokenType, lexicalState);
    }

    /*
     * Methods
     */

    @Override
    protected void reset(CharSequence charSequence) throws IOException {
        // start to trigger BINARY_INTEGER state
        CharSequence fullCharSequence = "0b" + charSequence;
        super.reset(fullCharSequence);
        // consume '0'
        flexLexer.advance();
        // consume 'b'
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
                { "0", ElixirTypes.VALID_BINARY_DIGITS, ElixirFlexLexer.BINARY_INTEGER },
                { "1", ElixirTypes.VALID_BINARY_DIGITS, ElixirFlexLexer.BINARY_INTEGER },
                { "2", ElixirTypes.INVALID_BINARY_DIGITS, ElixirFlexLexer.BINARY_INTEGER },
                { "3", ElixirTypes.INVALID_BINARY_DIGITS, ElixirFlexLexer.BINARY_INTEGER },
                { "4", ElixirTypes.INVALID_BINARY_DIGITS, ElixirFlexLexer.BINARY_INTEGER },
                { "5", ElixirTypes.INVALID_BINARY_DIGITS, ElixirFlexLexer.BINARY_INTEGER },
                { "6", ElixirTypes.INVALID_BINARY_DIGITS, ElixirFlexLexer.BINARY_INTEGER },
                { "7", ElixirTypes.INVALID_BINARY_DIGITS, ElixirFlexLexer.BINARY_INTEGER },
                { "8", ElixirTypes.INVALID_BINARY_DIGITS, ElixirFlexLexer.BINARY_INTEGER },
                { "9", ElixirTypes.INVALID_BINARY_DIGITS, ElixirFlexLexer.BINARY_INTEGER },
                { ":", ElixirTypes.COLON, ElixirFlexLexer.ATOM_START },
                { ";", ElixirTypes.SEMICOLON, INITIAL_STATE },
                { "<", ElixirTypes.RELATIONAL_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE },
                { "=", ElixirTypes.MATCH_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE },
                { ">", ElixirTypes.RELATIONAL_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE },
                { "?", TokenType.BAD_CHARACTER, INITIAL_STATE },
                { "@", ElixirTypes.AT_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE },
                { "A", ElixirTypes.INVALID_BINARY_DIGITS, ElixirFlexLexer.BINARY_INTEGER },
                { "B", ElixirTypes.INVALID_BINARY_DIGITS, ElixirFlexLexer.BINARY_INTEGER },
                { "C", ElixirTypes.INVALID_BINARY_DIGITS, ElixirFlexLexer.BINARY_INTEGER },
                { "D", ElixirTypes.INVALID_BINARY_DIGITS, ElixirFlexLexer.BINARY_INTEGER },
                { "E", ElixirTypes.INVALID_BINARY_DIGITS, ElixirFlexLexer.BINARY_INTEGER },
                { "F", ElixirTypes.INVALID_BINARY_DIGITS, ElixirFlexLexer.BINARY_INTEGER },
                { "G", ElixirTypes.INVALID_BINARY_DIGITS, ElixirFlexLexer.BINARY_INTEGER },
                { "H", ElixirTypes.INVALID_BINARY_DIGITS, ElixirFlexLexer.BINARY_INTEGER },
                { "I", ElixirTypes.INVALID_BINARY_DIGITS, ElixirFlexLexer.BINARY_INTEGER },
                { "J", ElixirTypes.INVALID_BINARY_DIGITS, ElixirFlexLexer.BINARY_INTEGER },
                { "K", ElixirTypes.INVALID_BINARY_DIGITS, ElixirFlexLexer.BINARY_INTEGER },
                { "L", ElixirTypes.INVALID_BINARY_DIGITS, ElixirFlexLexer.BINARY_INTEGER },
                { "M", ElixirTypes.INVALID_BINARY_DIGITS, ElixirFlexLexer.BINARY_INTEGER },
                { "N", ElixirTypes.INVALID_BINARY_DIGITS, ElixirFlexLexer.BINARY_INTEGER },
                { "O", ElixirTypes.INVALID_BINARY_DIGITS, ElixirFlexLexer.BINARY_INTEGER },
                { "P", ElixirTypes.INVALID_BINARY_DIGITS, ElixirFlexLexer.BINARY_INTEGER },
                { "Q", ElixirTypes.INVALID_BINARY_DIGITS, ElixirFlexLexer.BINARY_INTEGER },
                { "R", ElixirTypes.INVALID_BINARY_DIGITS, ElixirFlexLexer.BINARY_INTEGER },
                { "S", ElixirTypes.INVALID_BINARY_DIGITS, ElixirFlexLexer.BINARY_INTEGER },
                { "T", ElixirTypes.INVALID_BINARY_DIGITS, ElixirFlexLexer.BINARY_INTEGER },
                { "U", ElixirTypes.INVALID_BINARY_DIGITS, ElixirFlexLexer.BINARY_INTEGER },
                { "V", ElixirTypes.INVALID_BINARY_DIGITS, ElixirFlexLexer.BINARY_INTEGER },
                { "W", ElixirTypes.INVALID_BINARY_DIGITS, ElixirFlexLexer.BINARY_INTEGER },
                { "X", ElixirTypes.INVALID_BINARY_DIGITS, ElixirFlexLexer.BINARY_INTEGER },
                { "Y", ElixirTypes.INVALID_BINARY_DIGITS, ElixirFlexLexer.BINARY_INTEGER },
                { "Z", ElixirTypes.INVALID_BINARY_DIGITS, ElixirFlexLexer.BINARY_INTEGER },
                { "[", ElixirTypes.OPENING_BRACKET, INITIAL_STATE },
                { "\"", ElixirTypes.STRING_PROMOTER, ElixirFlexLexer.GROUP },
                { "\"\"\"", ElixirTypes.STRING_HEREDOC_PROMOTER, ElixirFlexLexer.GROUP_HEREDOC_START },
                { "\n", ElixirTypes.EOL, INITIAL_STATE },
                { "\r\n", ElixirTypes.EOL, INITIAL_STATE },
                { "]", ElixirTypes.CLOSING_BRACKET, INITIAL_STATE },
                { "^", ElixirTypes.UNARY_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE },
                { "_", ElixirTypes.IDENTIFIER, ElixirFlexLexer.KEYWORD_PAIR_MAYBE },
                { "`", TokenType.BAD_CHARACTER, INITIAL_STATE },
                { "a", ElixirTypes.INVALID_BINARY_DIGITS, ElixirFlexLexer.BINARY_INTEGER },
                { "b", ElixirTypes.INVALID_BINARY_DIGITS, ElixirFlexLexer.BINARY_INTEGER },
                { "c", ElixirTypes.INVALID_BINARY_DIGITS, ElixirFlexLexer.BINARY_INTEGER },
                { "d", ElixirTypes.INVALID_BINARY_DIGITS, ElixirFlexLexer.BINARY_INTEGER },
                { "e", ElixirTypes.INVALID_BINARY_DIGITS, ElixirFlexLexer.BINARY_INTEGER },
                { "f", ElixirTypes.INVALID_BINARY_DIGITS, ElixirFlexLexer.BINARY_INTEGER },
                { "g", ElixirTypes.INVALID_BINARY_DIGITS, ElixirFlexLexer.BINARY_INTEGER },
                { "h", ElixirTypes.INVALID_BINARY_DIGITS, ElixirFlexLexer.BINARY_INTEGER },
                { "i", ElixirTypes.INVALID_BINARY_DIGITS, ElixirFlexLexer.BINARY_INTEGER },
                { "j", ElixirTypes.INVALID_BINARY_DIGITS, ElixirFlexLexer.BINARY_INTEGER },
                { "k", ElixirTypes.INVALID_BINARY_DIGITS, ElixirFlexLexer.BINARY_INTEGER },
                { "l", ElixirTypes.INVALID_BINARY_DIGITS, ElixirFlexLexer.BINARY_INTEGER },
                { "m", ElixirTypes.INVALID_BINARY_DIGITS, ElixirFlexLexer.BINARY_INTEGER },
                { "n", ElixirTypes.INVALID_BINARY_DIGITS, ElixirFlexLexer.BINARY_INTEGER },
                { "o", ElixirTypes.INVALID_BINARY_DIGITS, ElixirFlexLexer.BINARY_INTEGER },
                { "p", ElixirTypes.INVALID_BINARY_DIGITS, ElixirFlexLexer.BINARY_INTEGER },
                { "q", ElixirTypes.INVALID_BINARY_DIGITS, ElixirFlexLexer.BINARY_INTEGER },
                { "r", ElixirTypes.INVALID_BINARY_DIGITS, ElixirFlexLexer.BINARY_INTEGER },
                { "s", ElixirTypes.INVALID_BINARY_DIGITS, ElixirFlexLexer.BINARY_INTEGER },
                { "t", ElixirTypes.INVALID_BINARY_DIGITS, ElixirFlexLexer.BINARY_INTEGER },
                { "u", ElixirTypes.INVALID_BINARY_DIGITS, ElixirFlexLexer.BINARY_INTEGER },
                { "v", ElixirTypes.INVALID_BINARY_DIGITS, ElixirFlexLexer.BINARY_INTEGER },
                { "w", ElixirTypes.INVALID_BINARY_DIGITS, ElixirFlexLexer.BINARY_INTEGER },
                { "x", ElixirTypes.INVALID_BINARY_DIGITS, ElixirFlexLexer.BINARY_INTEGER },
                { "y", ElixirTypes.INVALID_BINARY_DIGITS, ElixirFlexLexer.BINARY_INTEGER },
                { "z", ElixirTypes.INVALID_BINARY_DIGITS, ElixirFlexLexer.BINARY_INTEGER },
                { "{", TokenType.BAD_CHARACTER, INITIAL_STATE },
                { "|", ElixirTypes.PIPE_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE },
                { "}", TokenType.BAD_CHARACTER, INITIAL_STATE },
                { "~", ElixirTypes.TILDE, ElixirFlexLexer.SIGIL }
        });
    }
}
